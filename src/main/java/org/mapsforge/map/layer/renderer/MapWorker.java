/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.map.PausableThread;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.queue.JobQueue;

public class MapWorker extends PausableThread {
	private final DatabaseRenderer databaseRenderer;
	private final JobQueue<RendererJob> jobQueue;
	private final LayerManager layerManager;
	private final TileCache tileCache;

	public MapWorker(final TileCache tileCache, final JobQueue<RendererJob> jobQueue, final DatabaseRenderer databaseRenderer, final LayerManager layerManager) {
		super();

		this.tileCache = tileCache;
		this.jobQueue = jobQueue;
		this.databaseRenderer = databaseRenderer;
		this.layerManager = layerManager;
	}

	@Override
	protected void doWork() throws InterruptedException {
		final RendererJob rendererJob = this.jobQueue.remove();

		final Bitmap bitmap = this.databaseRenderer.executeJob(rendererJob);

		if (!isInterrupted() && bitmap != null) {
			this.tileCache.put(rendererJob, bitmap);
			this.layerManager.redrawLayers();
		}
	}

	@Override
	protected ThreadPriority getThreadPriority() {
		return ThreadPriority.BELOW_NORMAL;
	}

	@Override
	protected boolean hasWork() {
		return true;
	}
}
