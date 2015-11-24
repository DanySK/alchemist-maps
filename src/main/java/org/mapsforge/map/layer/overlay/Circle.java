/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.overlay;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

/**
 * A {@code Circle} consists of a center {@link LatLong} and a non-negative
 * radius in meters.
 * <p>
 * A {@code Circle} holds two {@link Paint} objects to allow for different
 * outline and filling. These paints define drawing parameters such as color,
 * stroke width, pattern and transparency.
 */
public class Circle extends Layer {
    private LatLong latLong;
    private Paint paintFill;
    private Paint paintStroke;
    private float radius;

    private static double metersToPixels(final double latitude, final float meters, final byte zoom) {
        return meters / MercatorProjection.calculateGroundResolution(latitude, zoom);
    }

    /**
     * @param latLong
     *            the initial center point of this circle (may be null).
     * @param radius
     *            the initial non-negative radius of this circle in meters.
     * @param paintFill
     *            the initial {@code Paint} used to fill this circle (may be
     *            null).
     * @param paintStroke
     *            the initial {@code Paint} used to stroke this circle (may be
     *            null).
     * @throws IllegalArgumentException
     *             if the given {@code radius} is negative or {@link Float#NaN}.
     */
    public Circle(final LatLong latLong, final float radius, final Paint paintFill, final Paint paintStroke) {
        super();

        this.latLong = latLong;
        setRadiusInternal(radius);
        this.paintFill = paintFill;
        this.paintStroke = paintStroke;
    }

    @Override
    public synchronized void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
        if (this.latLong == null || this.paintStroke == null && this.paintFill == null) {
            return;
        }

        final double latitude = this.latLong.getLatitude();
        final double longitude = this.latLong.getLongitude();
        final int pixelX = (int) (MercatorProjection.longitudeToPixelX(longitude, zoomLevel) - canvasPosition.getX());
        final int pixelY = (int) (MercatorProjection.latitudeToPixelY(latitude, zoomLevel) - canvasPosition.getY());
        final int radiusInPixel = (int) metersToPixels(latitude, this.radius, zoomLevel);

        final Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
        if (!canvasRectangle.intersectsCircle(pixelX, pixelY, radiusInPixel)) {
            return;
        }

        if (this.paintStroke != null) {
            canvas.drawCircle(pixelX, pixelY, radiusInPixel, this.paintStroke);
        }
        if (this.paintFill != null) {
            canvas.drawCircle(pixelX, pixelY, radiusInPixel, this.paintFill);
        }
    }

    /**
     * @return the center point of this circle (may be null).
     */
    public synchronized LatLong getLatLong() {
        return this.latLong;
    }

    /**
     * @return the {@code Paint} used to fill this circle (may be null).
     */
    public synchronized Paint getPaintFill() {
        return this.paintFill;
    }

    /**
     * @return the {@code Paint} used to stroke this circle (may be null).
     */
    public synchronized Paint getPaintStroke() {
        return this.paintStroke;
    }

    /**
     * @return the non-negative radius of this circle in meters.
     */
    public synchronized float getRadius() {
        return this.radius;
    }

    /**
     * @param latLong
     *            the new center point of this circle (may be null).
     */
    public synchronized void setLatLong(final LatLong latLong) {
        this.latLong = latLong;
    }

    /**
     * @param paintFill
     *            the new {@code Paint} used to fill this circle (may be null).
     */
    public synchronized void setPaintFill(final Paint paintFill) {
        this.paintFill = paintFill;
    }

    /**
     * @param paintStroke
     *            the new {@code Paint} used to stroke this circle (may be
     *            null).
     */
    public synchronized void setPaintStroke(final Paint paintStroke) {
        this.paintStroke = paintStroke;
    }

    /**
     * @param radius
     *            the new non-negative radius of this circle in meters.
     * @throws IllegalArgumentException
     *             if the given {@code radius} is negative or {@link Float#NaN}.
     */
    public synchronized void setRadius(final float radius) {
        setRadiusInternal(radius);
    }

    private void setRadiusInternal(final float radius) {
        if (radius < 0 || Float.isNaN(radius)) {
            throw new IllegalArgumentException("invalid radius: " + radius);
        }
        this.radius = radius;
    }
}
