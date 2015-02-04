/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.queue;

import java.util.Collection;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;

public final class QueueItemScheduler {
	private static final double PENALTY_PER_ZOOM_LEVEL = 10 * Tile.TILE_SIZE;

	private static double calculatePriority(final Tile tile, final MapPosition mapPosition) {
		final double tileLatitude = MercatorProjection.tileYToLatitude(tile.getTileY(), tile.getZoomLevel());
		final double tileLongitude = MercatorProjection.tileXToLongitude(tile.getTileX(), tile.getZoomLevel());

		final int halfTileSize = Tile.TILE_SIZE / 2;
		final double tilePixelX = MercatorProjection.longitudeToPixelX(tileLongitude, mapPosition.getZoomLevel()) + halfTileSize;
		final double tilePixelY = MercatorProjection.latitudeToPixelY(tileLatitude, mapPosition.getZoomLevel()) + halfTileSize;

		final LatLong latLong = mapPosition.getLatLong();
		final double mapPixelX = MercatorProjection.longitudeToPixelX(latLong.getLongitude(), mapPosition.getZoomLevel());
		final double mapPixelY = MercatorProjection.latitudeToPixelY(latLong.getLatitude(), mapPosition.getZoomLevel());

		final double diffPixel = Math.hypot(tilePixelX - mapPixelX, tilePixelY - mapPixelY);
		final int diffZoom = Math.abs(tile.getZoomLevel() - mapPosition.getZoomLevel());

		return diffPixel + PENALTY_PER_ZOOM_LEVEL * diffZoom;
	}

	public static <T extends Job> void schedule(final Collection<QueueItem<T>> queueItems, final MapPosition mapPosition) {
		for (final QueueItem<T> queueItem : queueItems) {
			queueItem.setPriority(calculatePriority(queueItem.getObject().getTile(), mapPosition));
		}
	}

	private QueueItemScheduler() {
		throw new IllegalStateException();
	}
}
