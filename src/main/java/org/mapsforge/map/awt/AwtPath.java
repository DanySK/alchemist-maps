/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.awt;

import java.awt.geom.Path2D;

import org.mapsforge.core.graphics.Path;

public class AwtPath implements Path {
    // final Polygon polygon = new Polygon();
    private final Path2D path = new Path2D.Double();
    private int pointsCount;

    @Override
    public void addPoint(final int x, final int y) {
        if (pointsCount == 0) {
            this.path.moveTo(x, y);
        } else {
            this.path.lineTo(x, y);
        }
        pointsCount++;
    }

    @Override
    public void clear() {
        this.path.reset();
        pointsCount = 0;
    }

    public Path2D getPath() {
        return path;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public boolean isEmpty() {
        return this.pointsCount == 0;
    }

    public void setPointsCount(final int p) {
        this.pointsCount = p;
    }
}
