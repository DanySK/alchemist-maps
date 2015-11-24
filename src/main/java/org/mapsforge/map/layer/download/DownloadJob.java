/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download;

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.queue.Job;

public class DownloadJob extends Job {
    public static final int PRIME = 31;
    private final TileSource tileSource;

    public DownloadJob(final Tile tile, final TileSource tileSource) {
        super(tile);

        if (tileSource == null) {
            throw new IllegalArgumentException("tileSource must not be null");
        }

        this.tileSource = tileSource;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (!(obj instanceof DownloadJob)) {
            return false;
        }
        final DownloadJob other = (DownloadJob) obj;
        if (!this.tileSource.equals(other.tileSource)) {
            return false;
        }
        return true;
    }

    public TileSource getTileSource() {
        return tileSource;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = PRIME * result + this.tileSource.hashCode();
        return result;
    }
}
