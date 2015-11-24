/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.overlay;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

/**
 * A {@code Marker} draws a {@link Bitmap} at a given geographical position.
 */
public class Marker extends Layer {
    private Bitmap bitmap;
    private final int dx;
    private final int dy;
    private LatLong latLong;

    /**
     * @param latLong
     *            the initial geographical coordinates of this marker (may be
     *            null).
     * @param bitmap
     *            the initial {@code Bitmap} of this marker (may be null).
     * @param dx
     *            the horizontal marker offset.
     * @param dy
     *            the vertical marker offset.
     */
    public Marker(final LatLong latLong, final Bitmap bitmap, final int dx, final int dy) {
        super();

        this.latLong = latLong;
        this.bitmap = bitmap;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public synchronized void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
        if (this.latLong == null || this.bitmap == null) {
            return;
        }

        final double pixelX = MercatorProjection.longitudeToPixelX(this.latLong.getLongitude(), zoomLevel);
        final double pixelY = MercatorProjection.latitudeToPixelY(this.latLong.getLatitude(), zoomLevel);

        final int left = (int) (pixelX - canvasPosition.getX() + this.dx - this.bitmap.getWidth() / 2);
        final int top = (int) (pixelY - canvasPosition.getY() + this.dy - this.bitmap.getHeight() / 2);
        final int right = left + this.bitmap.getWidth();
        final int bottom = top + this.bitmap.getHeight();

        final Rectangle bitmapRectangle = new Rectangle(left, top, right, bottom);
        final Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
        if (!canvasRectangle.intersects(bitmapRectangle)) {
            return;
        }

        canvas.drawBitmap(this.bitmap, left, top);
    }

    /**
     * @return the {@code Bitmap} of this marker (may be null).
     */
    public synchronized Bitmap getBitmap() {
        return this.bitmap;
    }

    /**
     * @return the geographical coordinates of this marker (may be null).
     */
    public synchronized LatLong getLatLong() {
        return this.latLong;
    }

    /**
     * @param bitmap
     *            the new {@code Bitmap} of this marker (may be null).
     */
    public synchronized void setBitmap(final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @param latLong
     *            the new geographical coordinates of this marker (may be null).
     */
    public synchronized void setLatLong(final LatLong latLong) {
        this.latLong = latLong;
    }
}
