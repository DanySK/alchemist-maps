/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tag;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.MapReadResult;
import org.mapsforge.map.reader.PointOfInterest;
import org.mapsforge.map.reader.Way;
import org.mapsforge.map.reader.header.MapFileInfo;
import org.mapsforge.map.rendertheme.RenderCallback;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.mapsforge.map.rendertheme.rule.RenderTheme;
import org.mapsforge.map.rendertheme.rule.RenderThemeHandler;
import org.xml.sax.SAXException;

/**
 * A DatabaseRenderer renders map tiles by reading from a {@link MapDatabase}.
 */
public class DatabaseRenderer implements RenderCallback {
	private static final Byte DEFAULT_START_ZOOM_LEVEL = Byte.valueOf((byte) 12);
	private static final byte LAYERS = 11;
	private static final Logger LOGGER = Logger.getLogger(DatabaseRenderer.class.getName());
	private static final double STROKE_INCREASE = 1.5;
	private static final byte STROKE_MIN_ZOOM_LEVEL = 12;
	private static final Tag TAG_NATURAL_WATER = new Tag("natural", "water");
	private static final Point[][] WATER_TILE_COORDINATES = getTilePixelCoordinates();
	private static final byte ZOOM_MAX = 22;

	private final List<PointTextContainer> areaLabels;

	private final CanvasRasterer canvasRasterer;

	private Point[][] coordinates;
	private RendererJob currentRendererJob;
	private List<List<ShapePaintContainer>> drawingLayers;
	private final GraphicFactory graphicFactory;
	private final LabelPlacement labelPlacement;
	private final MapDatabase mapDatabase;
	private List<PointTextContainer> nodes;
	private final List<SymbolContainer> pointSymbols;
	private Point poiPosition;
	private XmlRenderTheme previousJobTheme;
	private float previousTextScale;
	private byte previousZoomLevel;
	private RenderTheme renderTheme;
	private ShapeContainer shapeContainer;
	private final List<WayTextContainer> wayNames;
	private final List<List<List<ShapePaintContainer>>> ways;
	private final List<SymbolContainer> waySymbols;

	private static Point[][] getTilePixelCoordinates() {
		final Point point1 = new Point(0, 0);
		final Point point2 = new Point(Tile.TILE_SIZE, 0);
		final Point point3 = new Point(Tile.TILE_SIZE, Tile.TILE_SIZE);
		final Point point4 = new Point(0, Tile.TILE_SIZE);
		return new Point[][] { { point1, point2, point3, point4, point1 } };
	}

	private static byte getValidLayer(final byte layer) {
		if (layer < 0) {
			return 0;
		} else if (layer >= LAYERS) {
			return LAYERS - 1;
		} else {
			return layer;
		}
	}

	/**
	 * Constructs a new DatabaseRenderer.
	 * 
	 * @param mapDatabase
	 *            the MapDatabase from which the map data will be read.
	 */
	public DatabaseRenderer(final MapDatabase mapDatabase, final GraphicFactory graphicFactory) {
		this.mapDatabase = mapDatabase;
		this.graphicFactory = graphicFactory;

		this.canvasRasterer = new CanvasRasterer(graphicFactory);
		this.labelPlacement = new LabelPlacement();

		this.ways = new ArrayList<List<List<ShapePaintContainer>>>(LAYERS);
		this.wayNames = new ArrayList<WayTextContainer>(64);
		this.nodes = new ArrayList<PointTextContainer>(64);
		this.areaLabels = new ArrayList<PointTextContainer>(64);
		this.waySymbols = new ArrayList<SymbolContainer>(64);
		this.pointSymbols = new ArrayList<SymbolContainer>(64);
	}

	private void clearLists() {
		for (int i = this.ways.size() - 1; i >= 0; --i) {
			final List<List<ShapePaintContainer>> innerWayList = this.ways.get(i);
			for (int j = innerWayList.size() - 1; j >= 0; --j) {
				innerWayList.get(j).clear();
			}
		}

		this.areaLabels.clear();
		this.nodes.clear();
		this.pointSymbols.clear();
		this.wayNames.clear();
		this.waySymbols.clear();
	}

	private void createWayLists() {
		final int levels = this.renderTheme.getLevels();
		this.ways.clear();

		for (byte i = LAYERS - 1; i >= 0; --i) {
			final List<List<ShapePaintContainer>> innerWayList = new ArrayList<List<ShapePaintContainer>>(levels);
			for (int j = levels - 1; j >= 0; --j) {
				innerWayList.add(new ArrayList<ShapePaintContainer>(0));
			}
			this.ways.add(innerWayList);
		}
	}

	/**
	 * Called when a job needs to be executed.
	 * 
	 * @param rendererJob
	 *            the job that should be executed.
	 */
	public Bitmap executeJob(final RendererJob rendererJob) {
		this.currentRendererJob = rendererJob;

		final XmlRenderTheme jobTheme = rendererJob.getXmlRenderTheme();
		if (!jobTheme.equals(this.previousJobTheme)) {
			this.renderTheme = getRenderTheme(jobTheme);
			if (this.renderTheme == null) {
				this.previousJobTheme = null;
				return null;
			}
			createWayLists();
			this.previousJobTheme = jobTheme;
			this.previousZoomLevel = Byte.MIN_VALUE;
		}

		final byte zoomLevel = rendererJob.getTile().getZoomLevel();
		if (zoomLevel != this.previousZoomLevel) {
			setScaleStrokeWidth(zoomLevel);
			this.previousZoomLevel = zoomLevel;
		}

		final float textScale = rendererJob.getTextScale();
		if (Float.compare(textScale, this.previousTextScale) != 0) {
			this.renderTheme.scaleTextSize(textScale);
			this.previousTextScale = textScale;
		}

		if (this.mapDatabase != null) {
			final MapReadResult mapReadResult = this.mapDatabase.readMapData(rendererJob.getTile());
			processReadMapData(mapReadResult);
		}

		this.nodes = this.labelPlacement.placeLabels(this.nodes, this.pointSymbols, this.areaLabels, rendererJob.getTile());

		final Bitmap bitmap = this.graphicFactory.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE);
		this.canvasRasterer.setCanvasBitmap(bitmap);
		this.canvasRasterer.fill(this.renderTheme.getMapBackground());
		this.canvasRasterer.drawWays(this.ways);
		this.canvasRasterer.drawSymbols(this.waySymbols);
		this.canvasRasterer.drawSymbols(this.pointSymbols);
		this.canvasRasterer.drawWayNames(this.wayNames);
		this.canvasRasterer.drawNodes(this.nodes);
		this.canvasRasterer.drawNodes(this.areaLabels);

		clearLists();

		return bitmap;
	}

	private RenderTheme getRenderTheme(final XmlRenderTheme jobTheme) {
		try {
			return RenderThemeHandler.getRenderTheme(this.graphicFactory, jobTheme);
		} catch (final ParserConfigurationException e) {
			LOGGER.log(Level.SEVERE, null, e);
		} catch (final SAXException e) {
			LOGGER.log(Level.SEVERE, null, e);
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * @return the start point (may be null).
	 */
	public LatLong getStartPoint() {
		if (this.mapDatabase != null && this.mapDatabase.hasOpenFile()) {
			final MapFileInfo mapFileInfo = this.mapDatabase.getMapFileInfo();
			if (mapFileInfo.getStartPosition() != null) {
				return mapFileInfo.getStartPosition();
			}
			return mapFileInfo.getBoundingBox().getCenterPoint();
		}

		return null;
	}

	/**
	 * @return the start zoom level (may be null).
	 */
	public Byte getStartZoomLevel() {
		if (this.mapDatabase != null && this.mapDatabase.hasOpenFile()) {
			final MapFileInfo mapFileInfo = this.mapDatabase.getMapFileInfo();
			if (mapFileInfo.getStartZoomLevel() != null) {
				return mapFileInfo.getStartZoomLevel();
			}
		}

		return DEFAULT_START_ZOOM_LEVEL;
	}

	/**
	 * @return the maximum zoom level.
	 */
	public byte getZoomLevelMax() {
		return ZOOM_MAX;
	}

	private void processReadMapData(final MapReadResult mapReadResult) {
		if (mapReadResult == null) {
			return;
		}

		for (final PointOfInterest pointOfInterest : mapReadResult.pointOfInterests) {
			renderPointOfInterest(pointOfInterest);
		}

		for (final Way way : mapReadResult.ways) {
			renderWay(way);
		}

		if (mapReadResult.isWater) {
			renderWaterBackground();
		}
	}

	@Override
	public void renderArea(final Paint fill, final Paint stroke, final int level) {
		final List<ShapePaintContainer> list = this.drawingLayers.get(level);
		list.add(new ShapePaintContainer(this.shapeContainer, fill));
		list.add(new ShapePaintContainer(this.shapeContainer, stroke));
	}

	@Override
	public void renderAreaCaption(final String caption, final float verticalOffset, final Paint fill, final Paint stroke) {
		final Point centerPosition = GeometryUtils.calculateCenterOfBoundingBox(this.coordinates[0]);
		this.areaLabels.add(new PointTextContainer(caption, centerPosition.getX(), centerPosition.getY(), fill, stroke));
	}

	@Override
	public void renderAreaSymbol(final Bitmap symbol) {
		final Point centerPosition = GeometryUtils.calculateCenterOfBoundingBox(this.coordinates[0]);
		final int halfSymbolWidth = symbol.getWidth() / 2;
		final int halfSymbolHeight = symbol.getHeight() / 2;
		final double pointX = centerPosition.getX() - halfSymbolWidth;
		final double pointY = centerPosition.getY() - halfSymbolHeight;
		final Point shiftedCenterPosition = new Point(pointX, pointY);
		this.pointSymbols.add(new SymbolContainer(symbol, shiftedCenterPosition));
	}

	private void renderPointOfInterest(final PointOfInterest pointOfInterest) {
		this.drawingLayers = this.ways.get(getValidLayer(pointOfInterest.getLayer()));
		this.poiPosition = scaleLatLong(pointOfInterest.getPosition());
		this.renderTheme.matchNode(this, pointOfInterest.getTags(), this.currentRendererJob.getTile().getZoomLevel());
	}

	@Override
	public void renderPointOfInterestCaption(final String caption, final float verticalOffset, final Paint fill, final Paint stroke) {
		this.nodes.add(new PointTextContainer(caption, this.poiPosition.getX(), this.poiPosition.getY() + verticalOffset, fill, stroke));
	}

	@Override
	public void renderPointOfInterestCircle(final float radius, final Paint fill, final Paint stroke, final int level) {
		final List<ShapePaintContainer> list = this.drawingLayers.get(level);
		list.add(new ShapePaintContainer(new CircleContainer(this.poiPosition, radius), fill));
		list.add(new ShapePaintContainer(new CircleContainer(this.poiPosition, radius), stroke));
	}

	@Override
	public void renderPointOfInterestSymbol(final Bitmap symbol) {
		final int halfSymbolWidth = symbol.getWidth() / 2;
		final int halfSymbolHeight = symbol.getHeight() / 2;
		final double pointX = this.poiPosition.getX() - halfSymbolWidth;
		final double pointY = this.poiPosition.getY() - halfSymbolHeight;
		final Point shiftedCenterPosition = new Point(pointX, pointY);
		this.pointSymbols.add(new SymbolContainer(symbol, shiftedCenterPosition));
	}

	private void renderWaterBackground() {
		this.drawingLayers = this.ways.get(0);
		this.coordinates = WATER_TILE_COORDINATES;
		this.shapeContainer = new WayContainer(this.coordinates);
		this.renderTheme.matchClosedWay(this, Arrays.asList(TAG_NATURAL_WATER), this.currentRendererJob.getTile().getZoomLevel());
	}

	@Override
	public void renderWay(final Paint stroke, final int level) {
		this.drawingLayers.get(level).add(new ShapePaintContainer(this.shapeContainer, stroke));
	}

	private void renderWay(final Way way) {
		this.drawingLayers = this.ways.get(getValidLayer(way.getLayer()));
		// TODO what about the label position?

		final LatLong[][] latLongs = way.getLatLongs();
		this.coordinates = new Point[latLongs.length][];
		for (int i = 0; i < this.coordinates.length; ++i) {
			this.coordinates[i] = new Point[latLongs[i].length];

			for (int j = 0; j < this.coordinates[i].length; ++j) {
				this.coordinates[i][j] = scaleLatLong(latLongs[i][j]);
			}
		}
		this.shapeContainer = new WayContainer(this.coordinates);

		if (GeometryUtils.isClosedWay(this.coordinates[0])) {
			this.renderTheme.matchClosedWay(this, way.getTags(), this.currentRendererJob.getTile().getZoomLevel());
		} else {
			this.renderTheme.matchLinearWay(this, way.getTags(), this.currentRendererJob.getTile().getZoomLevel());
		}
	}

	@Override
	public void renderWaySymbol(final Bitmap symbolBitmap, final boolean alignCenter, final boolean repeatSymbol) {
		WayDecorator.renderSymbol(symbolBitmap, alignCenter, repeatSymbol, this.coordinates, this.waySymbols);
	}

	@Override
	public void renderWayText(final String textKey, final Paint fill, final Paint stroke) {
		WayDecorator.renderText(textKey, fill, stroke, this.coordinates, this.wayNames);
	}

	/**
	 * Converts the given LatLong into XY coordinates on the current object.
	 * 
	 * @param latLong
	 *            the LatLong to convert.
	 * @return the XY coordinates on the current object.
	 */
	private Point scaleLatLong(final LatLong latLong) {
		final double pixelX = MercatorProjection.longitudeToPixelX(latLong.getLongitude(), this.currentRendererJob.getTile().getZoomLevel()) - MercatorProjection.tileXToPixelX(this.currentRendererJob.getTile().getTileX());
		final double pixelY = MercatorProjection.latitudeToPixelY(latLong.getLatitude(), this.currentRendererJob.getTile().getZoomLevel()) - MercatorProjection.tileYToPixelY(this.currentRendererJob.getTile().getTileY());

		return new Point((float) pixelX, (float) pixelY);
	}

	/**
	 * Sets the scale stroke factor for the given zoom level.
	 * 
	 * @param zoomLevel
	 *            the zoom level for which the scale stroke factor should be
	 *            set.
	 */
	private void setScaleStrokeWidth(final byte zoomLevel) {
		final int zoomLevelDiff = Math.max(zoomLevel - STROKE_MIN_ZOOM_LEVEL, 0);
		this.renderTheme.scaleStrokeWidth((float) Math.pow(STROKE_INCREASE, zoomLevelDiff));
	}
}
