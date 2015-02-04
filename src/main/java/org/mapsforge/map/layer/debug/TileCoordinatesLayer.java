/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.debug;

import java.util.List;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerUtil;
import org.mapsforge.map.layer.TilePosition;

public class TileCoordinatesLayer extends Layer {
	private final Paint paint;

	private static Paint createPaint(final GraphicFactory graphicFactory) {
		final Paint paint = graphicFactory.createPaint();
		paint.setColor(graphicFactory.createColor(Color.BLACK));
		paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
		paint.setTextSize(20);
		return paint;
	}

	public TileCoordinatesLayer(final GraphicFactory graphicFactory) {
		super();

		this.paint = createPaint(graphicFactory);
	}

	@Override
	public void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
		final List<TilePosition> tilePositions = LayerUtil.getTilePositions(boundingBox, zoomLevel, canvasPosition);
		for (int i = tilePositions.size() - 1; i >= 0; --i) {
			drawTileCoordinates(tilePositions.get(i), canvas);
		}
	}

	private void drawTileCoordinates(final TilePosition tilePosition, final Canvas canvas) {
		final int x = (int) tilePosition.getPoint().getX() + 15;
		final int y = (int) tilePosition.getPoint().getY() + 30;
		final Tile tile = tilePosition.getTile();

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("X: ");
		stringBuilder.append(tile.getTileX());
		canvas.drawText(stringBuilder.toString(), x, y, this.paint);

		stringBuilder.setLength(0);
		stringBuilder.append("Y: ");
		stringBuilder.append(tile.getTileY());
		canvas.drawText(stringBuilder.toString(), x, y + 30, this.paint);

		stringBuilder.setLength(0);
		stringBuilder.append("Z: ");
		stringBuilder.append(tile.getZoomLevel());
		canvas.drawText(stringBuilder.toString(), x, y + 60, this.paint);
	}
}
