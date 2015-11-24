/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;

public final class LayerUtil {
    public static List<TilePosition> getTilePositions(final BoundingBox boundingBox, final byte zoomLevel, final Point canvasPosition) {
        final long tileLeft = MercatorProjection.longitudeToTileX(boundingBox.getMinLongitude(), zoomLevel);
        final long tileTop = MercatorProjection.latitudeToTileY(boundingBox.getMaxLatitude(), zoomLevel);
        final long tileRight = MercatorProjection.longitudeToTileX(boundingBox.getMaxLongitude(), zoomLevel);
        final long tileBottom = MercatorProjection.latitudeToTileY(boundingBox.getMinLatitude(), zoomLevel);

        final int initialCapacity = (int) ((tileRight - tileLeft + 1) * (tileBottom - tileTop + 1));
        final ArrayList<TilePosition> tilePositions = new ArrayList<TilePosition>(initialCapacity);

        for (long tileX = tileLeft; tileX <= tileRight; ++tileX) {
            for (long tileY = tileTop; tileY <= tileBottom; ++tileY) {
                final double pixelX = MercatorProjection.tileXToPixelX(tileX) - canvasPosition.getX();
                final double pixelY = MercatorProjection.tileYToPixelY(tileY) - canvasPosition.getY();

                tilePositions.add(new TilePosition(new Tile(tileX, tileY, zoomLevel), new Point(pixelX, pixelY)));
            }
        }

        return tilePositions;
    }

    private LayerUtil() {
        throw new IllegalStateException();
    }
}
