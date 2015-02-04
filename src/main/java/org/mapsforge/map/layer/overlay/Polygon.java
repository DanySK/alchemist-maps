/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.overlay;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Path;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

/**
 * A {@code Polygon} draws a connected series of line segments specified by a
 * list of {@link LatLong LatLongs}. If the first and the last {@code LatLong}
 * are not equal, the {@code Polygon} will be closed automatically.
 * <p>
 * A {@code Polygon} holds two {@link Paint} objects to allow for different
 * outline and filling. These paints define drawing parameters such as color,
 * stroke width, pattern and transparency.
 */
public class Polygon extends Layer {
	private final GraphicFactory graphicFactory;
	private final List<LatLong> latLongs = new CopyOnWriteArrayList<LatLong>();
	private Paint paintFill;
	private Paint paintStroke;

	/**
	 * @param paintFill
	 *            the initial {@code Paint} used to fill this polygon (may be
	 *            null).
	 * @param paintStroke
	 *            the initial {@code Paint} used to stroke this polygon (may be
	 *            null).
	 */
	public Polygon(final Paint paintFill, final Paint paintStroke, final GraphicFactory graphicFactory) {
		super();

		this.paintFill = paintFill;
		this.paintStroke = paintStroke;
		this.graphicFactory = graphicFactory;
	}

	@Override
	public synchronized void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
		if (this.latLongs.isEmpty() || this.paintStroke == null && this.paintFill == null) {
			return;
		}

		final Path path = this.graphicFactory.createPath();

		for (final LatLong latLong : this.latLongs) {
			final int x = (int) (MercatorProjection.longitudeToPixelX(latLong.getLongitude(), zoomLevel) - canvasPosition.getX());
			final int y = (int) (MercatorProjection.latitudeToPixelY(latLong.getLatitude(), zoomLevel) - canvasPosition.getY());
			path.addPoint(x, y);
		}

		if (this.paintStroke != null) {
			canvas.drawPath(path, this.paintStroke);
		}
		if (this.paintFill != null) {
			canvas.drawPath(path, this.paintFill);
		}
	}

	/**
	 * @return a thread-safe list of LatLongs in this polygon.
	 */
	public List<LatLong> getLatLongs() {
		return this.latLongs;
	}

	/**
	 * @return the {@code Paint} used to fill this polygon (may be null).
	 */
	public synchronized Paint getPaintFill() {
		return this.paintFill;
	}

	/**
	 * @return the {@code Paint} used to stroke this polygon (may be null).
	 */
	public synchronized Paint getPaintStroke() {
		return this.paintStroke;
	}

	/**
	 * @param paintFill
	 *            the new {@code Paint} used to fill this polygon (may be null).
	 */
	public synchronized void setPaintFill(final Paint paintFill) {
		this.paintFill = paintFill;
	}

	/**
	 * @param paintStroke
	 *            the new {@code Paint} used to stroke this polygon (may be
	 *            null).
	 */
	public synchronized void setPaintStroke(final Paint paintStroke) {
		this.paintStroke = paintStroke;
	}
}
