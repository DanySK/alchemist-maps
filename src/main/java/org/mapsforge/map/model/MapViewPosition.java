/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.model.common.Observable;
import org.mapsforge.map.model.common.Persistable;
import org.mapsforge.map.model.common.PreferencesFacade;

public class MapViewPosition extends Observable implements Persistable {
    private static final String LATITUDE = "latitude";
    private static final String LATITUDE_MAX = "latitudeMax";
    private static final String LATITUDE_MIN = "latitudeMin";
    private static final String LONGITUDE = "longitude";
    private static final String LONGITUDE_MAX = "longitudeMax";
    private static final String LONGITUDE_MIN = "longitudeMin";
    private static final String ZOOM_LEVEL = "zoomLevel";
    private static final String ZOOM_LEVEL_MAX = "zoomLevelMax";
    private static final String ZOOM_LEVEL_MIN = "zoomLevelMin";

    private double latitude;

    private double longitude;
    private BoundingBox mapLimit;
    private byte zoomLevel;
    private byte zoomLevelMax;
    private byte zoomLevelMin;

    private static boolean isNan(final double... values) {
        for (final double value : values) {
            if (Double.isNaN(value)) {
                return true;
            }
        }

        return false;
    }

    public MapViewPosition() {
        super();

        this.zoomLevelMax = Byte.MAX_VALUE;
    }

    /**
     * @return the current center position of the map.
     */
    public synchronized LatLong getCenter() {
        return new LatLong(this.latitude, this.longitude);
    }

    /**
     * @return the current limit of the map (might be null).
     */
    public synchronized BoundingBox getMapLimit() {
        return this.mapLimit;
    }

    /**
     * @return the current center position and zoom level of the map.
     */
    public synchronized MapPosition getMapPosition() {
        return new MapPosition(getCenter(), this.zoomLevel);
    }

    /**
     * @return the current zoom level of the map.
     */
    public synchronized byte getZoomLevel() {
        return this.zoomLevel;
    }

    public synchronized byte getZoomLevelMax() {
        return this.zoomLevelMax;
    }

    public synchronized byte getZoomLevelMin() {
        return this.zoomLevelMin;
    }

    @Override
    public synchronized void init(final PreferencesFacade preferencesFacade) {
        this.latitude = preferencesFacade.getDouble(LATITUDE, 0);
        this.longitude = preferencesFacade.getDouble(LONGITUDE, 0);

        final double maxLatitude = preferencesFacade.getDouble(LATITUDE_MAX, Double.NaN);
        final double minLatitude = preferencesFacade.getDouble(LATITUDE_MIN, Double.NaN);
        final double maxLongitude = preferencesFacade.getDouble(LONGITUDE_MAX, Double.NaN);
        final double minLongitude = preferencesFacade.getDouble(LONGITUDE_MIN, Double.NaN);

        if (isNan(maxLatitude, minLatitude, maxLongitude, minLongitude)) {
            this.mapLimit = null;
        } else {
            this.mapLimit = new BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
        }

        this.zoomLevel = preferencesFacade.getByte(ZOOM_LEVEL, (byte) 0);
        this.zoomLevelMax = preferencesFacade.getByte(ZOOM_LEVEL_MAX, Byte.MAX_VALUE);
        this.zoomLevelMin = preferencesFacade.getByte(ZOOM_LEVEL_MIN, (byte) 0);
    }

    /**
     * Moves the center position of the map by the given amount of pixels.
     * 
     * @param moveHorizontal
     *            the amount of pixels to move this MapViewPosition
     *            horizontally.
     * @param moveVertical
     *            the amount of pixels to move this MapViewPosition vertically.
     */
    public void moveCenter(final double moveHorizontal, final double moveVertical) {
        synchronized (this) {
            double pixelX = MercatorProjection.longitudeToPixelX(this.longitude, this.zoomLevel) - moveHorizontal;
            double pixelY = MercatorProjection.latitudeToPixelY(this.latitude, this.zoomLevel) - moveVertical;

            final long mapSize = MercatorProjection.getMapSize(this.zoomLevel);
            pixelX = Math.min(Math.max(0, pixelX), mapSize);
            pixelY = Math.min(Math.max(0, pixelY), mapSize);

            final double newLatitude = MercatorProjection.pixelYToLatitude(pixelY, this.zoomLevel);
            final double newLongitude = MercatorProjection.pixelXToLongitude(pixelX, this.zoomLevel);
            setCenterInternal(new LatLong(newLatitude, newLongitude));
        }
        notifyObservers();
    }

    @Override
    public synchronized void save(final PreferencesFacade preferencesFacade) {
        preferencesFacade.putDouble(LATITUDE, this.latitude);
        preferencesFacade.putDouble(LONGITUDE, this.longitude);

        if (this.mapLimit == null) {
            preferencesFacade.putDouble(LATITUDE_MAX, Double.NaN);
            preferencesFacade.putDouble(LATITUDE_MIN, Double.NaN);
            preferencesFacade.putDouble(LONGITUDE_MAX, Double.NaN);
            preferencesFacade.putDouble(LONGITUDE_MIN, Double.NaN);
        } else {
            preferencesFacade.putDouble(LATITUDE_MAX, this.mapLimit.getMaxLatitude());
            preferencesFacade.putDouble(LATITUDE_MIN, this.mapLimit.getMinLatitude());
            preferencesFacade.putDouble(LONGITUDE_MAX, this.mapLimit.getMaxLongitude());
            preferencesFacade.putDouble(LONGITUDE_MIN, this.mapLimit.getMinLongitude());
        }

        preferencesFacade.putInt(ZOOM_LEVEL, this.zoomLevel);
        preferencesFacade.putInt(ZOOM_LEVEL_MAX, this.zoomLevelMax);
        preferencesFacade.putInt(ZOOM_LEVEL_MIN, this.zoomLevelMin);
    }

    /**
     * Sets the new center position of the map.
     */
    public void setCenter(final LatLong latLong) {
        synchronized (this) {
            setCenterInternal(latLong);
        }
        notifyObservers();
    }

    private void setCenterInternal(final LatLong latLong) {
        if (this.mapLimit == null) {
            this.latitude = latLong.getLatitude();
            this.longitude = latLong.getLongitude();
        } else {
            this.latitude = Math.max(Math.min(latLong.getLatitude(), this.mapLimit.getMaxLatitude()), this.mapLimit.getMinLatitude());
            this.longitude = Math.max(Math.min(latLong.getLongitude(), this.mapLimit.getMaxLongitude()), this.mapLimit.getMinLongitude());
        }
    }

    /**
     * Sets the new limit of the map (might be null).
     */
    public void setMapLimit(final BoundingBox mapLimit) {
        synchronized (this) {
            this.mapLimit = mapLimit;
        }
        notifyObservers();
    }

    /**
     * Sets the new center position and zoom level of the map.
     */
    public void setMapPosition(final MapPosition mapPosition) {
        synchronized (this) {
            setCenterInternal(mapPosition.getLatLong());
            setZoomLevelInternal(mapPosition.getZoomLevel());
        }
        notifyObservers();
    }

    /**
     * Sets the new zoom level of the map.
     * 
     * @throws IllegalArgumentException
     *             if the zoom level is negative.
     */
    public void setZoomLevel(final byte zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException("zoomLevel must not be negative: " + zoomLevel);
        }
        synchronized (this) {
            setZoomLevelInternal(zoomLevel);
        }
        notifyObservers();
    }

    private void setZoomLevelInternal(final int zoomLevel) {
        this.zoomLevel = (byte) Math.max(Math.min(zoomLevel, this.zoomLevelMax), this.zoomLevelMin);
    }

    public void setZoomLevelMax(final byte zoomLevelMax) {
        if (zoomLevelMax < 0) {
            throw new IllegalArgumentException("zoomLevelMax must not be negative: " + zoomLevelMax);
        }
        synchronized (this) {
            if (zoomLevelMax < this.zoomLevelMin) {
                throw new IllegalArgumentException("zoomLevelMax must be >= zoomLevelMin: " + zoomLevelMax);
            }
            this.zoomLevelMax = zoomLevelMax;
        }
        notifyObservers();
    }

    public void setZoomLevelMin(final byte zoomLevelMin) {
        if (zoomLevelMin < 0) {
            throw new IllegalArgumentException("zoomLevelMin must not be negative: " + zoomLevelMin);
        }
        synchronized (this) {
            if (zoomLevelMin > this.zoomLevelMax) {
                throw new IllegalArgumentException("zoomLevelMin must be <= zoomLevelMax: " + zoomLevelMin);
            }
            this.zoomLevelMin = zoomLevelMin;
        }
        notifyObservers();
    }

    /**
     * Changes the current zoom level by the given value if possible.
     */
    public void zoom(final byte zoomLevelDiff) {
        synchronized (this) {
            setZoomLevelInternal(this.zoomLevel + zoomLevelDiff);
        }
        notifyObservers();
    }

    /**
     * Increases the current zoom level by one if possible.
     */
    public void zoomIn() {
        zoom((byte) 1);
    }

    /**
     * Decreases the current zoom level by one if possible.
     */
    public void zoomOut() {
        zoom((byte) -1);
    }
}
