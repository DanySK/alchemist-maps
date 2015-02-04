/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.TileLayer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.model.MapViewPosition;

public class TileDownloadLayer extends TileLayer<DownloadJob> {
	private static final int DOWNLOAD_THREADS_MAX = 8;

	private final TileDownloadThread[] tileDownloadThreads;
	private final TileSource tileSource;

	public TileDownloadLayer(final TileCache tileCache, final MapViewPosition mapViewPosition, final TileSource tileSource, final LayerManager layerManager, final GraphicFactory graphicFactory) {
		super(tileCache, mapViewPosition, graphicFactory);

		if (tileSource == null) {
			throw new IllegalArgumentException("tileSource must not be null");
		} else if (layerManager == null) {
			throw new IllegalArgumentException("layerManager must not be null");
		}

		this.tileSource = tileSource;

		final int numberOfDownloadThreads = Math.min(tileSource.getParallelRequestsLimit(), DOWNLOAD_THREADS_MAX);
		this.tileDownloadThreads = new TileDownloadThread[numberOfDownloadThreads];
		for (int i = 0; i < numberOfDownloadThreads; ++i) {
			final TileDownloadThread tileDownloadThread = new TileDownloadThread(tileCache, this.jobQueue, layerManager, graphicFactory);
			tileDownloadThread.start();
			this.tileDownloadThreads[i] = tileDownloadThread;
		}
	}

	@Override
	protected DownloadJob createJob(final Tile tile) {
		return new DownloadJob(tile, this.tileSource);
	}

	@Override
	public void destroy() {
		for (final TileDownloadThread tileDownloadThread : this.tileDownloadThreads) {
			tileDownloadThread.interrupt();
		}

		super.destroy();
	}
}
