/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.debug;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

public class TileGridLayer extends Layer {
    private final Paint paint;

    private static Paint createPaint(final GraphicFactory graphicFactory) {
        final Paint paint = graphicFactory.createPaint();
        paint.setColor(graphicFactory.createColor(Color.BLACK));
        paint.setStrokeWidth(3);
        paint.setStyle(Style.STROKE);
        return paint;
    }

    public TileGridLayer(final GraphicFactory graphicFactory) {
        super();

        this.paint = createPaint(graphicFactory);
    }

    @Override
    public void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
        final long tileLeft = MercatorProjection.longitudeToTileX(boundingBox.getMinLongitude(), zoomLevel);
        final long tileTop = MercatorProjection.latitudeToTileY(boundingBox.getMaxLatitude(), zoomLevel);
        final long tileRight = MercatorProjection.longitudeToTileX(boundingBox.getMaxLongitude(), zoomLevel);
        final long tileBottom = MercatorProjection.latitudeToTileY(boundingBox.getMinLatitude(), zoomLevel);

        final int pixelX1 = (int) (MercatorProjection.tileXToPixelX(tileLeft) - canvasPosition.getX());
        final int pixelY1 = (int) (MercatorProjection.tileYToPixelY(tileTop) - canvasPosition.getY());
        final int pixelX2 = (int) (MercatorProjection.tileXToPixelX(tileRight) - canvasPosition.getX() + Tile.TILE_SIZE);
        final int pixelY2 = (int) (MercatorProjection.tileYToPixelY(tileBottom) - canvasPosition.getY() + Tile.TILE_SIZE);

        for (int lineX = pixelX1; lineX <= pixelX2 + 1; lineX += Tile.TILE_SIZE) {
            canvas.drawLine(lineX, pixelY1, lineX, pixelY2, this.paint);
        }

        for (int lineY = pixelY1; lineY <= pixelY2 + 1; lineY += Tile.TILE_SIZE) {
            canvas.drawLine(pixelX1, lineY, pixelX2, lineY, this.paint);
        }
    }
}
