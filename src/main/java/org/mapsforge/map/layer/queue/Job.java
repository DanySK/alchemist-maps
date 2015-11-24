/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.queue;

import org.mapsforge.core.model.Tile;

public class Job {
    private static final int PRIME = 31;
    private final Tile tile;

    protected Job(final Tile tile) {
        if (tile == null) {
            throw new IllegalArgumentException("tile must not be null");
        }

        this.tile = tile;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Job)) {
            return false;
        }
        final Job other = (Job) obj;
        if (!this.tile.equals(other.tile)) {
            return false;
        }
        return true;
    }

    public Tile getTile() {
        return tile;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = PRIME * result + this.tile.hashCode();
        return result;
    }
}
