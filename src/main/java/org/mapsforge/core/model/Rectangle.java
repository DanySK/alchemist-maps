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
 * A Rectangle represents an immutable set of four double coordinates.
 * 
 */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int EXTIMED_SIZE = 100;

    private final double b;
    private final double l;
    private final double r;
    private final double t;

    private int hash;

    /**
     * @param left
     *            left
     * @param top
     *            top
     * @param right
     *            right
     * @param bottom
     *            bottom
     */
    public Rectangle(final double left, final double top, final double right, final double bottom) {
        if (left > right) {
            throw new IllegalArgumentException("left: " + left + ", right: " + right);
        } else if (top > bottom) {
            throw new IllegalArgumentException("top: " + top + ", bottom: " + bottom);
        }

        this.l = left;
        this.t = top;
        this.r = right;
        this.b = bottom;
    }

    /**
     * @param point point
     * @return true if this Rectangle contains the given point, false otherwise.
     */
    public boolean contains(final Point point) {
        return this.l <= point.getX() && this.r >= point.getX() && this.t <= point.getY() && this.b >= point.getY();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Rectangle)) {
            return false;
        }
        final Rectangle other = (Rectangle) obj;
        return Double.doubleToLongBits(l) == Double.doubleToLongBits(other.l) && Double.doubleToLongBits(t) == Double.doubleToLongBits(other.t) && Double.doubleToLongBits(r) == Double.doubleToLongBits(other.r) && Double.doubleToLongBits(b) == Double.doubleToLongBits(other.b);
    }

    /**
     * @return lowest point
     */
    public double getBottom() {
        return b;
    }

    /**
     * @return a new Point at the horizontal and vertical center of this
     *         Rectangle.
     */
    public Point getCenter() {
        return new Point(getCenterX(), getCenterY());
    }

    /**
     * @return the horizontal center of this Rectangle.
     */
    public double getCenterX() {
        return (this.l + this.r) / 2;
    }

    /**
     * @return the vertical center of this Rectangle.
     */
    public double getCenterY() {
        return (this.t + this.b) / 2;
    }

    /**
     * @return height
     */
    public double getHeight() {
        return this.b - this.t;
    }

    /**
     * @return leftmost point
     */
    public double getLeft() {
        return l;
    }

    /**
     * @return rightmost point
     */
    public double getRight() {
        return r;
    }

    /**
     * @return upper point
     */
    public double getTop() {
        return t;
    }

    /**
     * @return width
     */
    public double getWidth() {
        return this.r - this.l;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = HashUtils.djb2int32(b, l, r, t);
        }
        return hash;
    }

    /**
     * @param rectangle
     *            other rectangle
     * @return true if this Rectangle intersects with the given Rectangle, false
     *         otherwise.
     */
    public boolean intersects(final Rectangle rectangle) {
        if (this == rectangle) {
            return true;
        }

        return this.l <= rectangle.r && rectangle.l <= this.r && this.t <= rectangle.b && rectangle.t <= this.b;
    }

    /**
     * @param pointX
     *            circle center x
     * @param pointY
     *            circle center y
     * @param radius
     *            circle radius
     * @return true if intersects the give circle
     */
    public boolean intersectsCircle(final double pointX, final double pointY, final double radius) {
        final double halfWidth = getWidth() / 2;
        final double halfHeight = getHeight() / 2;

        final double centerDistanceX = Math.abs(pointX - getCenterX());
        final double centerDistanceY = Math.abs(pointY - getCenterY());

        // is the circle is far enough away from the rectangle?
        if (centerDistanceX > halfWidth + radius) {
            return false;
        } else if (centerDistanceY > halfHeight + radius) {
            return false;
        }

        // is the circle close enough to the rectangle?
        if (centerDistanceX <= halfWidth) {
            return true;
        } else if (centerDistanceY <= halfHeight) {
            return true;
        }

        final double cornerDistanceX = centerDistanceX - halfWidth;
        final double cornerDistanceY = centerDistanceY - halfHeight;
        return cornerDistanceX * cornerDistanceX + cornerDistanceY * cornerDistanceY <= radius * radius;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
        stringBuilder.append("left=");
        stringBuilder.append(this.l);
        stringBuilder.append(", top=");
        stringBuilder.append(this.t);
        stringBuilder.append(", right=");
        stringBuilder.append(this.r);
        stringBuilder.append(", bottom=");
        stringBuilder.append(this.b);
        return stringBuilder.toString();
    }
}
