/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download;

import it.unibo.alchemist.utils.L;

import java.io.IOException;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.PausableThread;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.queue.JobQueue;

class TileDownloadThread extends PausableThread {
	private final GraphicFactory graphicFactory;
	private final JobQueue<DownloadJob> jobQueue;
	private final LayerManager layerManager;
	private final TileCache tileCache;

	TileDownloadThread(final TileCache tileCache, final JobQueue<DownloadJob> jobQueue, final LayerManager layerManager, final GraphicFactory graphicFactory) {
		super();

		this.tileCache = tileCache;
		this.jobQueue = jobQueue;
		this.layerManager = layerManager;
		this.graphicFactory = graphicFactory;
	}

	@Override
	protected void doWork() throws InterruptedException {
		final DownloadJob downloadJob = this.jobQueue.remove();

		try {
			final TileDownloader tileDownloader = new TileDownloader(downloadJob, this.graphicFactory);
			final Bitmap bitmap = tileDownloader.downloadImage();

			this.tileCache.put(downloadJob, bitmap);
			this.layerManager.redrawLayers();
		} catch (final IOException e) {
			L.warn(e);
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
