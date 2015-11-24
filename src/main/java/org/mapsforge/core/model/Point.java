/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.model;

import java.io.Serializable;

import org.danilopianini.lang.HashUtils;

/**
 * A Point represents an immutable pair of double coordinates.
 * 
 */
public class Point implements Comparable<Point>, Serializable {

    private static final long serialVersionUID = 4582904260719816947L;
    private static final int EXTIMED_SIZE = 50;

    private final double x;
    private final double y;

    private int hash;

    /**
     * @param xp
     *            the x coordinate of this point.
     * @param yp
     *            the y coordinate of this point.
     */
    public Point(final double xp, final double yp) {
        this.x = xp;
        this.y = yp;
    }

    @Override
    public int compareTo(final Point point) {
        if (this.x > point.x) {
            return 1;
        } else if (this.x < point.x) {
            return -1;
        } else if (this.y > point.y) {
            return 1;
        } else if (this.y < point.y) {
            return -1;
        }
        return 0;
    }

    /**
     * @param pt
     *            point
     * @return the Euclidean distance from this point to the given point.
     */
    public double distance(final Point pt) {
        return Math.hypot(this.x - pt.x, this.y - pt.y);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Point)) {
            return false;
        }
        final Point other = (Point) obj;
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x) && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
    }

    /**
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * @return y
     */
    public double getY() {
        return y;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = HashUtils.djb2int32(x, y);
        }
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
        stringBuilder.append("x=");
        stringBuilder.append(this.x);
        stringBuilder.append(", y=");
        stringBuilder.append(this.y);
        return stringBuilder.toString();
    }
}
