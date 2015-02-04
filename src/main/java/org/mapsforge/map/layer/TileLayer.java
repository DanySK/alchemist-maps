/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer;

import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.queue.Job;
import org.mapsforge.map.layer.queue.JobQueue;
import org.mapsforge.map.model.MapViewPosition;

public abstract class TileLayer<T extends Job> extends Layer {
	private final Matrix matrix;
	private final TileCache tileCache;
	protected final JobQueue<T> jobQueue;

	public TileLayer(final TileCache tileCache, final MapViewPosition mapViewPosition, final GraphicFactory graphicFactory) {
		super();

		if (tileCache == null) {
			throw new IllegalArgumentException("tileCache must not be null");
		} else if (mapViewPosition == null) {
			throw new IllegalArgumentException("mapViewPosition must not be null");
		}

		this.tileCache = tileCache;
		this.jobQueue = new JobQueue<T>(mapViewPosition);
		this.matrix = graphicFactory.createMatrix();
	}

	protected abstract T createJob(Tile tile);

	@Override
	public void destroy() {
		this.tileCache.destroy();
	}

	@Override
	public void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
		final List<TilePosition> tilePositions = LayerUtil.getTilePositions(boundingBox, zoomLevel, canvasPosition);

		for (int i = tilePositions.size() - 1; i >= 0; --i) {
			final TilePosition tilePosition = tilePositions.get(i);
			final Point point = tilePosition.getPoint();
			final Tile tile = tilePosition.getTile();
			final Bitmap bitmap = this.tileCache.get(createJob(tile));

			if (bitmap == null) {
				this.jobQueue.add(createJob(tile));
				drawParentTileBitmap(canvas, point, tile);
			} else {
				canvas.drawBitmap(bitmap, (int) point.getX(), (int) point.getY());
			}
		}

		this.jobQueue.notifyWorkers();
	}

	private void drawParentTileBitmap(final Canvas canvas, final Point point, final Tile tile) {
		final Tile cachedParentTile = getCachedParentTile(tile, 4);
		if (cachedParentTile != null) {
			final Bitmap bitmap = this.tileCache.get(createJob(cachedParentTile));
			if (bitmap != null) {
				final long translateX = tile.getShiftX(cachedParentTile) * Tile.TILE_SIZE;
				final long translateY = tile.getShiftY(cachedParentTile) * Tile.TILE_SIZE;
				final byte zoomLevelDiff = (byte) (tile.getZoomLevel() - cachedParentTile.getZoomLevel());
				final float scaleFactor = (float) Math.pow(2, zoomLevelDiff);

				this.matrix.reset();
				this.matrix.translate((float) (point.getX() - translateX), (float) (point.getY() - translateY));
				this.matrix.scale(scaleFactor, scaleFactor);
				canvas.drawBitmap(bitmap, this.matrix);
			}
		}
	}

	/**
	 * @return the first parent object of the given object whose tileCacheBitmap
	 *         is cached (may be null).
	 */
	private Tile getCachedParentTile(final Tile tile, final int level) {
		if (level == 0) {
			return null;
		}

		final Tile parentTile = tile.getParent();
		if (parentTile == null) {
			return null;
		} else if (this.tileCache.containsKey(createJob(parentTile))) {
			return parentTile;
		}

		return getCachedParentTile(parentTile, level - 1);
	}
}
