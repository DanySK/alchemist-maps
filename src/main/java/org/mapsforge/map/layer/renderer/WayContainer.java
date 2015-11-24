/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.model.Point;

public class WayContainer implements ShapeContainer {
    private final Point[][] coordinates;

    public WayContainer(final Point[][] coordinates) {
        this.coordinates = coordinates;
    }

    public Point[][] getCoordinates() {
        return coordinates;
    }

    @Override
    public ShapeType getShapeType() {
        return ShapeType.WAY;
    }

}
