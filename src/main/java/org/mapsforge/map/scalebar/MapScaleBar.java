/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.scalebar;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.model.MapViewPosition;

/**
 * A MapScaleBar displays the ratio of a distance on the map to the
 * corresponding distance on the ground.
 */
public class MapScaleBar {
    private static final int BITMAP_HEIGHT = 50;
    private static final int BITMAP_WIDTH = 150;
    private static final double LATITUDE_REDRAW_THRESHOLD = 0.2;
    private static final int MARGIN_BOTTOM = 5;
    private static final int MARGIN_LEFT = 5;

    private Adapter adapter;
    private final GraphicFactory graphicFactory;
    private MapPosition mapPosition;
    private final Bitmap mapScaleBitmap;
    private final Canvas mapScaleCanvas;
    private final MapViewPosition mapViewPosition;
    private final Paint paintScaleBar;
    private final Paint paintScaleBarStroke;
    private final Paint paintScaleText;
    private final Paint paintScaleTextStroke;
    private boolean redrawNeeded;
    private boolean visible;

    public MapScaleBar(final MapViewPosition mapViewPosition, final GraphicFactory graphicFactory) {
        this.mapViewPosition = mapViewPosition;
        this.graphicFactory = graphicFactory;

        this.mapScaleBitmap = graphicFactory.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT);
        this.mapScaleCanvas = graphicFactory.createCanvas();
        this.mapScaleCanvas.setBitmap(this.mapScaleBitmap);
        this.adapter = Metric.getInstance();

        this.paintScaleBar = createScaleBarPaint(Color.BLACK, 3);
        this.paintScaleBarStroke = createScaleBarPaint(Color.WHITE, 5);
        this.paintScaleText = createTextPaint(Color.BLACK, 0);
        this.paintScaleTextStroke = createTextPaint(Color.WHITE, 2);
    }

    private Paint createScaleBarPaint(final Color color, final float strokeWidth) {
        final Paint paint = this.graphicFactory.createPaint();
        paint.setColor(this.graphicFactory.createColor(color));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Cap.SQUARE);
        return paint;
    }

    private Paint createTextPaint(final Color color, final float strokeWidth) {
        final Paint paint = this.graphicFactory.createPaint();
        paint.setColor(this.graphicFactory.createColor(color));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Style.STROKE);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(20);
        return paint;
    }

    public void draw(final Canvas canvas) {
        if (!this.visible) {
            return;
        }

        redraw();

        final int top = canvas.getHeight() - BITMAP_HEIGHT - MARGIN_BOTTOM;
        canvas.drawBitmap(this.mapScaleBitmap, MARGIN_LEFT, top);
    }

    /**
     * Redraws the map scale bitmap with the given parameters.
     * 
     * @param scaleBarLength
     *            the length of the map scale bar in pixels.
     * @param mapScaleValue
     *            the map scale value in meters.
     */
    private void draw(final int scaleBarLength, final int mapScaleValue) {
        this.mapScaleBitmap.fillColor(this.graphicFactory.createColor(Color.TRANSPARENT));

        drawScaleBar(scaleBarLength, this.paintScaleBarStroke);
        drawScaleBar(scaleBarLength, this.paintScaleBar);

        final String scaleText = this.adapter.getScaleText(mapScaleValue);
        drawScaleText(scaleText, this.paintScaleTextStroke);
        drawScaleText(scaleText, this.paintScaleText);
    }

    private void drawScaleBar(final int scaleBarLength, final Paint paint) {
        this.mapScaleCanvas.drawLine(7, 25, scaleBarLength + 3, 25, paint);
        this.mapScaleCanvas.drawLine(5, 10, 5, 40, paint);
        this.mapScaleCanvas.drawLine(scaleBarLength + 5, 10, scaleBarLength + 5, 40, paint);
    }

    private void drawScaleText(final String scaleText, final Paint paint) {
        this.mapScaleCanvas.drawText(scaleText, 12, 18, paint);
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    private boolean isRedrawNecessary() {
        if (this.redrawNeeded || this.mapPosition == null) {
            return true;
        }

        final MapPosition currentMapPosition = this.mapViewPosition.getMapPosition();
        if (currentMapPosition.getZoomLevel() != this.mapPosition.getZoomLevel()) {
            return true;
        }

        final double latitudeDiff = Math.abs(currentMapPosition.getLatLong().getLatitude() - this.mapPosition.getLatLong().getLatitude());
        return latitudeDiff > LATITUDE_REDRAW_THRESHOLD;
    }

    public boolean isVisible() {
        return this.visible;
    }

    private void redraw() {
        if (!isRedrawNecessary()) {
            return;
        }

        this.mapPosition = this.mapViewPosition.getMapPosition();
        double groundResolution = MercatorProjection.calculateGroundResolution(this.mapPosition.getLatLong().getLatitude(), this.mapPosition.getZoomLevel());

        groundResolution = groundResolution / this.adapter.getMeterRatio();
        final int[] scaleBarValues = this.adapter.getScaleBarValues();

        int scaleBarLength = 0;
        int mapScaleValue = 0;

        for (final int scaleBarValue : scaleBarValues) {
            mapScaleValue = scaleBarValue;
            scaleBarLength = (int) (mapScaleValue / groundResolution);
            if (scaleBarLength < BITMAP_WIDTH - 10) {
                break;
            }
        }

        draw(scaleBarLength, mapScaleValue);
        this.redrawNeeded = false;
    }

    public void setAdapter(final Adapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter must not be null");
        }
        this.adapter = adapter;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
