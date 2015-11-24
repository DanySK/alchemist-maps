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

/**
 * A BoundingBox represents an immutable set of two latitude and two longitude
 * coordinates.
 * 
 * 
 */
public class BoundingBox implements Serializable {
    private static final int EXTIMED_SIZE = 100;
    private static final int PRIME = 31, SHIFT = 32;
    private static final long serialVersionUID = 1L;

    /**
     * The maximum latitude coordinate of this BoundingBox in degrees.
     */
    private final double maxLatitude;
    /**
     * The maximum longitude coordinate of this BoundingBox in degrees.
     */
    private final double maxLongitude;
    /**
     * The minimum latitude coordinate of this BoundingBox in degrees.
     */
    private final double minLatitude;
    /**
     * The minimum longitude coordinate of this BoundingBox in degrees.
     */
    private final double minLongitude;

    /**
     * Creates a new BoundingBox from a comma-separated string of coordinates in
     * the order minLat, minLon, maxLat, maxLon. All coordinate values must be
     * in degrees.
     * 
     * @param boundingBoxString
     *            the string that describes the BoundingBox.
     * @return a new BoundingBox with the given coordinates.
     */
    public static BoundingBox fromString(final String boundingBoxString) {
        final double[] coordinates = CoordinatesUtil.parseCoordinateString(boundingBoxString, 4);
        return new BoundingBox(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
    }

    /**
     * @param minLat
     *            the minimum latitude coordinate in degrees.
     * @param minLong
     *            the minimum longitude coordinate in degrees.
     * @param maxLat
     *            the maximum latitude coordinate in degrees.
     * @param maxLong
     *            the maximum longitude coordinate in degrees.
     */
    public BoundingBox(final double minLat, final double minLong, final double maxLat, final double maxLong) {
        CoordinatesUtil.validateLatitude(minLat);
        CoordinatesUtil.validateLongitude(minLong);
        CoordinatesUtil.validateLatitude(maxLat);
        CoordinatesUtil.validateLongitude(maxLong);

        if (minLat > maxLat) {
            throw new IllegalArgumentException("invalid latitude range: " + minLat + ' ' + maxLat);
        } else if (minLong > maxLong) {
            throw new IllegalArgumentException("invalid longitude range: " + minLong + ' ' + maxLong);
        }

        this.minLatitude = minLat;
        this.minLongitude = minLong;
        this.maxLatitude = maxLat;
        this.maxLongitude = maxLong;
    }

    /**
     * @param latLong
     *            the LatLong whose coordinates should be checked.
     * @return true if this BoundingBox contains the given LatLong, false
     *         otherwise.
     */
    public boolean contains(final LatLong latLong) {
        return this.minLatitude <= latLong.getLatitude() && this.maxLatitude >= latLong.getLatitude() && this.minLongitude <= latLong.getLongitude() && this.maxLongitude >= latLong.getLongitude();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof BoundingBox)) {
            return false;
        }
        final BoundingBox other = (BoundingBox) obj;
        return Double.doubleToLongBits(this.maxLatitude) != Double.doubleToLongBits(other.maxLatitude) || Double.doubleToLongBits(this.maxLongitude) != Double.doubleToLongBits(other.maxLongitude) || Double.doubleToLongBits(this.minLatitude) != Double.doubleToLongBits(other.minLatitude) || Double.doubleToLongBits(this.minLongitude) != Double.doubleToLongBits(other.minLongitude);
    }

    /**
     * @return a new LatLong at the horizontal and vertical center of this
     *         BoundingBox.
     */
    public LatLong getCenterPoint() {
        final double latitudeOffset = (this.maxLatitude - this.minLatitude) / 2;
        final double longitudeOffset = (this.maxLongitude - this.minLongitude) / 2;
        return new LatLong(this.minLatitude + latitudeOffset, this.minLongitude + longitudeOffset);
    }

    /**
     * @return the latitude span of this BoundingBox in degrees.
     */
    public double getLatitudeSpan() {
        return this.maxLatitude - this.minLatitude;
    }

    /**
     * @return the longitude span of this BoundingBox in degrees.
     */
    public double getLongitudeSpan() {
        return this.maxLongitude - this.minLongitude;
    }

    /**
     * @return maxLatitude
     */
    public double getMaxLatitude() {
        return maxLatitude;
    }

    /**
     * @return maxLongitude
     */
    public double getMaxLongitude() {
        return maxLongitude;
    }

    /**
     * @return minLatitude
     */
    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * @return minLongitude
     */
    public double getMinLongitude() {
        return minLongitude;
    }

    @Override
    public int hashCode() {
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.maxLatitude);
        result = PRIME * result + (int) (temp ^ temp >>> SHIFT);
        temp = Double.doubleToLongBits(this.maxLongitude);
        result = PRIME * result + (int) (temp ^ temp >>> SHIFT);
        temp = Double.doubleToLongBits(this.minLatitude);
        result = PRIME * result + (int) (temp ^ temp >>> SHIFT);
        temp = Double.doubleToLongBits(this.minLongitude);
        result = PRIME * result + (int) (temp ^ temp >>> SHIFT);
        return result;
    }

    /**
     * @param boundingBox
     *            the BoundingBox which should be checked for intersection with
     *            this BoundingBox.
     * @return true if this BoundingBox intersects with the given BoundingBox,
     *         false otherwise.
     */
    public boolean intersects(final BoundingBox boundingBox) {
        if (this == boundingBox) {
            return true;
        }

        return this.maxLatitude >= boundingBox.minLatitude && this.maxLongitude >= boundingBox.minLongitude && this.minLatitude <= boundingBox.maxLatitude && this.minLongitude <= boundingBox.maxLongitude;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
        stringBuilder.append("minLatitude=");
        stringBuilder.append(this.minLatitude);
        stringBuilder.append(", minLongitude=");
        stringBuilder.append(this.minLongitude);
        stringBuilder.append(", maxLatitude=");
        stringBuilder.append(this.maxLatitude);
        stringBuilder.append(", maxLongitude=");
        stringBuilder.append(this.maxLongitude);
        return stringBuilder.toString();
    }
}
