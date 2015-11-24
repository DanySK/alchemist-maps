/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.controller;

import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.Observer;
import org.mapsforge.map.view.FrameBuffer;

public class FrameBufferController implements Observer {
    private final FrameBuffer frameBuffer;
    private Dimension lastMapViewDimension;
    private double lastOverdrawFactor;
    private final Model model;

    private static Dimension calculateFrameBufferDimension(final Dimension mapViewDimension, final double overdrawFactor) {
        final int width = (int) (mapViewDimension.getWidth() * overdrawFactor);
        final int height = (int) (mapViewDimension.getHeight() * overdrawFactor);
        return new Dimension(width, height);
    }

    private static Point getPixel(final LatLong latLong, final byte zoomLevel) {
        final double pixelX = MercatorProjection.longitudeToPixelX(latLong.getLongitude(), zoomLevel);
        final double pixelY = MercatorProjection.latitudeToPixelY(latLong.getLatitude(), zoomLevel);
        return new Point(pixelX, pixelY);
    }

    public FrameBufferController(final FrameBuffer frameBuffer, final Model model) {
        this.frameBuffer = frameBuffer;
        this.model = model;

        model.getFrameBufferModel().addObserver(this);
        model.getMapViewModel().addObserver(this);
        model.getMapViewPosition().addObserver(this);
    }

    public void dispose() {
        model.getFrameBufferModel().removeObserver(this);
        model.getMapViewModel().removeObserver(this);
        model.getMapViewPosition().removeObserver(this);
    }

    private void adjustFrameBufferMatrix(final MapPosition mapPositionFrameBuffer, final Dimension mapViewDimension) {
        final MapPosition mapPosition = this.model.getMapViewPosition().getMapPosition();

        final Point pointFrameBuffer = getPixel(mapPositionFrameBuffer.getLatLong(), mapPosition.getZoomLevel());
        final Point pointMapPosition = getPixel(mapPosition.getLatLong(), mapPosition.getZoomLevel());
        final float diffX = (float) (pointFrameBuffer.getX() - pointMapPosition.getX());
        final float diffY = (float) (pointFrameBuffer.getY() - pointMapPosition.getY());

        final int zoomLevelDiff = mapPosition.getZoomLevel() - mapPositionFrameBuffer.getZoomLevel();
        final float scaleFactor = (float) Math.pow(2, zoomLevelDiff);

        this.frameBuffer.adjustMatrix(diffX, diffY, scaleFactor, mapViewDimension);
    }

    private boolean dimensionChangeNeeded(final Dimension mapViewDimension, final double overdrawFactor) {
        if (Double.compare(overdrawFactor, this.lastOverdrawFactor) != 0) {
            return true;
        } else if (!mapViewDimension.equals(this.lastMapViewDimension)) {
            return true;
        }
        return false;
    }

    @Override
    public void onChange() {
        final Dimension mapViewDimension = this.model.getMapViewModel().getDimension();
        if (mapViewDimension != null) {
            final double overdrawFactor = this.model.getFrameBufferModel().getOverdrawFactor();
            if (dimensionChangeNeeded(mapViewDimension, overdrawFactor)) {
                this.frameBuffer.setDimension(calculateFrameBufferDimension(mapViewDimension, overdrawFactor));
                this.lastMapViewDimension = mapViewDimension;
                this.lastOverdrawFactor = overdrawFactor;
            }

            synchronized (this.frameBuffer) {
                final MapPosition mapPositionFrameBuffer = this.model.getFrameBufferModel().getMapPosition();
                if (mapPositionFrameBuffer != null) {
                    adjustFrameBufferMatrix(mapPositionFrameBuffer, mapViewDimension);
                }
            }
        }
    }
}
