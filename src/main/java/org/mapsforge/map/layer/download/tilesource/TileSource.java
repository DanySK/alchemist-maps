/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download.tilesource;

import java.net.MalformedURLException;
import java.net.URL;

import org.mapsforge.core.model.Tile;

public interface TileSource {
	/**
	 * @return the maximum number of parallel requests which this
	 *         {@code TileSource} supports.
	 */
	int getParallelRequestsLimit();

	/**
	 * @return the download URL for the given {@code Tile}.
	 */
	URL getTileUrl(Tile tile) throws MalformedURLException;

	/**
	 * @return the maximum zoom level which this {@code TileSource} supports.
	 */
	byte getZoomLevelMax();

	/**
	 * @return the minimum zoom level which this {@code TileSource} supports.
	 */
	byte getZoomLevelMin();
}
