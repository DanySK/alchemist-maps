/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer;

import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;

public class TilePosition {
    private final Point point;
    private final Tile tile;

    public TilePosition(final Tile tile, final Point point) {
        this.tile = tile;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public Tile getTile() {
        return tile;
    }
}
