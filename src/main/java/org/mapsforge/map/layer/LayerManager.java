/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.PausableThread;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.view.FrameBuffer;
import org.mapsforge.map.view.MapView;

public class LayerManager extends PausableThread {
    private static final Logger LOGGER = Logger.getLogger(LayerManager.class.getName());
    private static final int MILLISECONDS_PER_FRAME = 50;

    private final int backgroundColor;

    private final Canvas drawingCanvas;

    private final List<Layer> layers;
    private final MapView mapView;
    private final MapViewPosition mapViewPosition;
    private boolean redrawNeeded;

    private static BoundingBox getBoundingBox(final MapPosition mapPosition, final Canvas canvas) {
        final double pixelX = MercatorProjection.longitudeToPixelX(mapPosition.getLatLong().getLongitude(), mapPosition.getZoomLevel());
        final double pixelY = MercatorProjection.latitudeToPixelY(mapPosition.getLatLong().getLatitude(), mapPosition.getZoomLevel());

        final int halfCanvasWidth = canvas.getWidth() / 2;
        final int halfCanvasHeight = canvas.getHeight() / 2;
        final long mapSize = MercatorProjection.getMapSize(mapPosition.getZoomLevel());

        final double pixelXMin = Math.max(0, pixelX - halfCanvasWidth);
        final double pixelYMin = Math.max(0, pixelY - halfCanvasHeight);
        final double pixelXMax = Math.min(mapSize, pixelX + halfCanvasWidth);
        final double pixelYMax = Math.min(mapSize, pixelY + halfCanvasHeight);

        final double minLatitude = MercatorProjection.pixelYToLatitude(pixelYMax, mapPosition.getZoomLevel());
        final double minLongitude = MercatorProjection.pixelXToLongitude(pixelXMin, mapPosition.getZoomLevel());
        final double maxLatitude = MercatorProjection.pixelYToLatitude(pixelYMin, mapPosition.getZoomLevel());
        final double maxLongitude = MercatorProjection.pixelXToLongitude(pixelXMax, mapPosition.getZoomLevel());

        return new BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
    }

    private static Point getCanvasPosition(final MapPosition mapPosition, final Canvas canvas) {
        final LatLong centerPoint = mapPosition.getLatLong();
        final byte zoomLevel = mapPosition.getZoomLevel();

        final int halfCanvasWidth = canvas.getWidth() / 2;
        final int halfCanvasHeight = canvas.getHeight() / 2;

        final double pixelX = MercatorProjection.longitudeToPixelX(centerPoint.getLongitude(), zoomLevel) - halfCanvasWidth;
        final double pixelY = MercatorProjection.latitudeToPixelY(centerPoint.getLatitude(), zoomLevel) - halfCanvasHeight;
        return new Point(pixelX, pixelY);
    }

    public LayerManager(final MapView mapView, final MapViewPosition mapViewPosition, final GraphicFactory graphicFactory) {
        super();

        this.mapView = mapView;
        this.mapViewPosition = mapViewPosition;

        this.drawingCanvas = graphicFactory.createCanvas();
        this.layers = new CopyOnWriteArrayList<Layer>();
        this.backgroundColor = graphicFactory.createColor(Color.WHITE);
    }

    @Override
    protected void afterRun() {
        for (final Layer layer : this.getLayers()) {
            layer.destroy();
        }
    }

    @Override
    protected void doWork() throws InterruptedException {
        final long startTime = System.nanoTime();
        this.redrawNeeded = false;

        final FrameBuffer frameBuffer = this.mapView.getFrameBuffer();
        final Bitmap bitmap = frameBuffer.getDrawingBitmap();
        if (bitmap != null) {
            bitmap.fillColor(this.backgroundColor);
            this.drawingCanvas.setBitmap(bitmap);

            final MapPosition mapPosition = this.mapViewPosition.getMapPosition();
            final BoundingBox boundingBox = getBoundingBox(mapPosition, this.drawingCanvas);
            final Point canvasPosition = getCanvasPosition(mapPosition, this.drawingCanvas);

            for (final Layer layer : this.getLayers()) {
                if (layer.isVisible()) {
                    layer.draw(boundingBox, mapPosition.getZoomLevel(), this.drawingCanvas, canvasPosition);
                }
            }

            frameBuffer.frameFinished(mapPosition);
            this.mapView.repaint();
        }

        final long elapsedMilliseconds = (System.nanoTime() - startTime) / 1000000;
        final long timeSleep = MILLISECONDS_PER_FRAME - elapsedMilliseconds;

        if (timeSleep > 1 && !isInterrupted()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "sleeping (ms): " + timeSleep);
            }
            sleep(timeSleep);
        }
    }

    public List<Layer> getLayers() {
        return this.layers;
    }

    @Override
    protected ThreadPriority getThreadPriority() {
        return ThreadPriority.NORMAL;
    }

    @Override
    protected boolean hasWork() {
        return this.redrawNeeded;
    }

    /**
     * Requests an asynchronous redrawing of all layers.
     */
    public void redrawLayers() {
        this.redrawNeeded = true;
        synchronized (this) {
            notify();
        }
    }
}
