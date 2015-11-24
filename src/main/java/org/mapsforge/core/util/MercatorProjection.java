/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.util;

import org.mapsforge.core.model.Tile;

/**
 * An implementation of the spherical Mercator projection.
 * 
 */
public final class MercatorProjection {
    /**
     * The circumference of the earth at the equator in meters.
     */
    public static final double EARTH_CIRCUMFERENCE = 40075016.686;

    /**
     * Maximum possible latitude coordinate of the map.
     */
    public static final double LATITUDE_MAX = 85.05112877980659;

    /**
     * Minimum possible latitude coordinate of the map.
     */
    public static final double LATITUDE_MIN = -LATITUDE_MAX;

    /**
     * Calculates the distance on the ground that is represented by a single
     * pixel on the map.
     * 
     * @param latitude
     *            the latitude coordinate at which the resolution should be
     *            calculated.
     * @param zoomLevel
     *            the zoom level at which the resolution should be calculated.
     * @return the ground resolution at the given latitude and zoom level.
     */
    public static double calculateGroundResolution(final double latitude, final byte zoomLevel) {
        final long mapSize = getMapSize(zoomLevel);
        return Math.cos(latitude * (Math.PI / 180)) * EARTH_CIRCUMFERENCE / mapSize;
    }

    /**
     * Computes the amount of latitude degrees for a given distance in pixel at
     * a given zoom level.
     * 
     * @param deltaPixel
     *            the delta in pixel
     * @param lat
     *            the latitude
     * @param zoom
     *            the zoom level
     * @return the delta in degrees
     */
    public static double deltaLat(final double deltaPixel, final double lat, final byte zoom) {
        final double pixelY = latitudeToPixelY(lat, zoom);
        final double lat2 = pixelYToLatitude(pixelY + deltaPixel, zoom);

        return Math.abs(lat2 - lat);
    }

    /**
     * @param zoomLevel
     *            the zoom level for which the size of the world map should be
     *            returned.
     * @return the horizontal and vertical size of the map in pixel at the given
     *         zoom level.
     */
    public static long getMapSize(final byte zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException("zoom level must not be negative: " + zoomLevel);
        }
        return (long) Tile.TILE_SIZE << Math.min(zoomLevel, 22);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a pixel Y coordinate at a
     * certain zoom level.
     * 
     * @param latitude
     *            the latitude coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the pixel Y coordinate of the latitude value.
     */
    public static double latitudeToPixelY(final double latitude, final byte zoomLevel) {
        final double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        final long mapSize = getMapSize(zoomLevel);
        final double pixelY = (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * mapSize;
        return Math.min(Math.max(0, pixelY), mapSize);
    }

    /**
     * Converts a latitude coordinate (in degrees) to a tile Y number at a
     * certain zoom level.
     * 
     * @param latitude
     *            the latitude coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the tile Y number of the latitude value.
     */
    public static long latitudeToTileY(final double latitude, final byte zoomLevel) {
        return pixelYToTileY(latitudeToPixelY(latitude, zoomLevel), zoomLevel);
    }

    /**
     * Converts a longitude coordinate (in degrees) to a pixel X coordinate at a
     * certain zoom level.
     * 
     * @param longitude
     *            the longitude coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the pixel X coordinate of the longitude value.
     */
    public static double longitudeToPixelX(final double longitude, final byte zoomLevel) {
        final long mapSize = getMapSize(zoomLevel);
        return (longitude + 180) / 360 * mapSize;
    }

    /**
     * Converts a longitude coordinate (in degrees) to the tile X number at a
     * certain zoom level.
     * 
     * @param longitude
     *            the longitude coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the tile X number of the longitude value.
     */
    public static long longitudeToTileX(final double longitude, final byte zoomLevel) {
        return pixelXToTileX(longitudeToPixelX(longitude, zoomLevel), zoomLevel);
    }

    /**
     * Converts a pixel X coordinate at a certain zoom level to a longitude
     * coordinate.
     * 
     * @param pixelX
     *            the pixel X coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the longitude value of the pixel X coordinate.
     */
    public static double pixelXToLongitude(final double pixelX, final byte zoomLevel) {
        final long mapSize = getMapSize(zoomLevel);
        if (pixelX < 0 || pixelX > mapSize) {
            throw new IllegalArgumentException("invalid pixelX coordinate at zoom level " + zoomLevel + ": " + pixelX);
        }
        return 360 * (pixelX / mapSize - 0.5);
    }

    /**
     * Converts a pixel X coordinate to the tile X number.
     * 
     * @param pixelX
     *            the pixel X coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the tile X number.
     */
    public static long pixelXToTileX(final double pixelX, final byte zoomLevel) {
        return (long) Math.min(Math.max(pixelX / Tile.TILE_SIZE, 0), Math.pow(2, zoomLevel) - 1);
    }

    /**
     * Converts a pixel Y coordinate at a certain zoom level to a latitude
     * coordinate.
     * 
     * @param pixelY
     *            the pixel Y coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the latitude value of the pixel Y coordinate.
     */
    public static double pixelYToLatitude(final double pixelY, final byte zoomLevel) {
        final long mapSize = getMapSize(zoomLevel);
        if (pixelY < 0 || pixelY > mapSize) {
            throw new IllegalArgumentException("invalid pixelY coordinate at zoom level " + zoomLevel + ": " + pixelY);
        }
        final double y = 0.5 - pixelY / mapSize;
        return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
    }

    /**
     * Converts a pixel Y coordinate to the tile Y number.
     * 
     * @param pixelY
     *            the pixel Y coordinate that should be converted.
     * @param zoomLevel
     *            the zoom level at which the coordinate should be converted.
     * @return the tile Y number.
     */
    public static long pixelYToTileY(final double pixelY, final byte zoomLevel) {
        return (long) Math.min(Math.max(pixelY / Tile.TILE_SIZE, 0), Math.pow(2, zoomLevel) - 1);
    }

    /**
     * Converts a tile X number at a certain zoom level to a longitude
     * coordinate.
     * 
     * @param tileX
     *            the tile X number that should be converted.
     * @param zoomLevel
     *            the zoom level at which the number should be converted.
     * @return the longitude value of the tile X number.
     */
    public static double tileXToLongitude(final long tileX, final byte zoomLevel) {
        return pixelXToLongitude(tileX * Tile.TILE_SIZE, zoomLevel);
    }

    /**
     * @param tileX
     *            the tile X number that should be converted.
     * @return the pixel X coordinate for the given tile X number.
     */
    public static long tileXToPixelX(final long tileX) {
        return tileX * Tile.TILE_SIZE;
    }

    /**
     * Converts a tile Y number at a certain zoom level to a latitude
     * coordinate.
     * 
     * @param tileY
     *            the tile Y number that should be converted.
     * @param zoomLevel
     *            the zoom level at which the number should be converted.
     * @return the latitude value of the tile Y number.
     */
    public static double tileYToLatitude(final long tileY, final byte zoomLevel) {
        return pixelYToLatitude(tileY * Tile.TILE_SIZE, zoomLevel);
    }

    /**
     * @param tileY
     *            the tile Y number that should be converted.
     * @return the pixel Y coordinate for the given tile Y number.
     */
    public static long tileYToPixelY(final long tileY) {
        return tileY * Tile.TILE_SIZE;
    }

    private MercatorProjection() {
        throw new IllegalStateException();
    }
}
