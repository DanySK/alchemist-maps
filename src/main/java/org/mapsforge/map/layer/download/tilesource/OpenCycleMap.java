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

public class OpenCycleMap extends AbstractTileSource {
    public static final OpenCycleMap INSTANCE = new OpenCycleMap("tile.opencyclemap.org", 80);
    private static final int PARALLEL_REQUESTS_LIMIT = 8;
    private static final String PROTOCOL = "http";
    private static final int ZOOM_LEVEL_MAX = 18;
    private static final int ZOOM_LEVEL_MIN = 0;

    public OpenCycleMap(final String hostName, final int port) {
        super(hostName, port);
    }

    @Override
    public int getParallelRequestsLimit() {
        return PARALLEL_REQUESTS_LIMIT;
    }

    @Override
    public URL getTileUrl(final Tile tile) throws MalformedURLException {
        final StringBuilder stringBuilder = new StringBuilder(32);

        stringBuilder.append("/cycle/");
        stringBuilder.append(tile.getZoomLevel());
        stringBuilder.append('/');
        stringBuilder.append(tile.getTileX());
        stringBuilder.append('/');
        stringBuilder.append(tile.getTileY());
        stringBuilder.append(".png");

        return new URL(PROTOCOL, getHostName(), this.getPort(), stringBuilder.toString());
    }

    @Override
    public byte getZoomLevelMax() {
        return ZOOM_LEVEL_MAX;
    }

    @Override
    public byte getZoomLevelMin() {
        return ZOOM_LEVEL_MIN;
    }
}
