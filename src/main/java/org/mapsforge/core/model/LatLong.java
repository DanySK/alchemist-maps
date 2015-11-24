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
 * A LatLong represents an immutable pair of latitude and longitude coordinates.
 * 
 * 
 */
public class LatLong implements Comparable<LatLong>, Serializable {
    private static final double EQUATORIAL_RADIUS = 6378137.0;
    private static final int ANGLE = 360;
    private static final long serialVersionUID = 1L;
    private static final int EXTIMED_SIZE = 50;
    /**
     * The latitude coordinate of this LatLong in degrees.
     */
    private final double latitude;

    /**
     * The longitude coordinate of this LatLong in degrees.
     */
    private final double longitude;

    /**
     * Creates a new LatLong from a comma-separated string of coordinates in the
     * order latitude, longitude. All coordinate values must be in degrees.
     * 
     * @param latLongString
     *            the string that describes the LatLong.
     * @return a new LatLong with the given coordinates.
     */
    public static LatLong fromString(final String latLongString) {
        final double[] coordinates = CoordinatesUtil.parseCoordinateString(latLongString, 2);
        return new LatLong(coordinates[0], coordinates[1]);
    }

    /**
     * Calculates the amount of degrees of latitude for a given distance in
     * meters.
     * 
     * @param meters
     *            distance in meters
     * @return latitude degrees
     */
    public static double latitudeDistance(final int meters) {
        return meters * ANGLE / (2 * Math.PI * EQUATORIAL_RADIUS);
    }

    /**
     * Calculates the amount of degrees of longitude for a given distance in
     * meters.
     * 
     * @param meters
     *            distance in meters
     * @param latitude
     *            the latitude at which the calculation should be performed
     * @return longitude degrees
     */
    public static double longitudeDistance(final int meters, final double latitude) {
        return meters * ANGLE / (2 * Math.PI * EQUATORIAL_RADIUS * Math.cos(Math.toRadians(latitude)));
    }

    /**
     * @param lat
     *            the latitude coordinate in degrees.
     * @param lng
     *            the longitude coordinate in degrees.
     */
    public LatLong(final double lat, final double lng) {
        CoordinatesUtil.validateLatitude(lat);
        CoordinatesUtil.validateLongitude(lng);

        this.latitude = lat;
        this.longitude = lng;
    }

    @Override
    public int compareTo(final LatLong latLong) {
        if (this.longitude > latLong.longitude) {
            return 1;
        } else if (this.longitude < latLong.longitude) {
            return -1;
        } else if (this.latitude > latLong.latitude) {
            return 1;
        } else if (this.latitude < latLong.latitude) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof LatLong)) {
            return false;
        }
        final LatLong other = (LatLong) obj;
        return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(other.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(other.longitude);
    }

    /**
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    @Override
    public int hashCode() {
        return HashUtils.djb2int32(latitude, longitude);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
        stringBuilder.append("latitude=");
        stringBuilder.append(this.latitude);
        stringBuilder.append(", longitude=");
        stringBuilder.append(this.longitude);
        return stringBuilder.toString();
    }
}
