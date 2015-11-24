/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.view;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.model.FrameBufferModel;

public class FrameBuffer {
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Dimension dimension;
    private final FrameBufferModel frameBufferModel;
    private final GraphicFactory graphicFactory;
    private final Matrix matrix;

    public FrameBuffer(final FrameBufferModel frameBufferModel, final GraphicFactory graphicFactory) {
        this.frameBufferModel = frameBufferModel;
        this.graphicFactory = graphicFactory;

        this.matrix = graphicFactory.createMatrix();
    }

    public synchronized void adjustMatrix(final float diffX, final float diffY, final float scaleFactor, final Dimension mapViewDimension) {
        if (this.dimension == null) {
            return;
        }

        this.matrix.reset();

        centerFrameBufferToMapView(mapViewDimension);
        scale(scaleFactor);
        this.matrix.translate(diffX, diffY);
    }

    private void centerFrameBufferToMapView(final Dimension mapViewDimension) {
        final float dx = (this.dimension.getWidth() - mapViewDimension.getWidth()) / -2f;
        final float dy = (this.dimension.getHeight() - mapViewDimension.getHeight()) / -2f;
        this.matrix.translate(dx, dy);
    }

    public synchronized void draw(final Canvas canvas) {
        if (this.bitmap1 != null) {
            canvas.drawBitmap(this.bitmap1, this.matrix);
        }
    }

    public synchronized void frameFinished(final MapPosition frameMapPosition) {
        // swap both bitmap references
        final Bitmap bitmapTemp = this.bitmap1;
        this.bitmap1 = this.bitmap2;
        this.bitmap2 = bitmapTemp;

        this.frameBufferModel.setMapPosition(frameMapPosition);
    }

    /**
     * @return the bitmap of the second frame to draw on (may be null).
     */
    public synchronized Bitmap getDrawingBitmap() {
        return this.bitmap2;
    }

    private void scale(final float scaleFactor) {
        if (scaleFactor != 1) {
            // the pivot point is the coordinate which remains unchanged by the
            // translation
            final float pivotScaleFactor = scaleFactor - 1;
            final float pivotX = this.dimension.getWidth() / -2f * pivotScaleFactor;
            final float pivotY = this.dimension.getHeight() / -2f * pivotScaleFactor;
            this.matrix.translate(pivotX, pivotY);
            this.matrix.scale(scaleFactor, scaleFactor);
        }
    }

    public synchronized void setDimension(final Dimension dimension) {
        this.dimension = dimension;

        if (dimension.getWidth() > 0 && dimension.getHeight() > 0) {
            this.bitmap1 = this.graphicFactory.createBitmap(dimension.getWidth(), dimension.getHeight());
            this.bitmap2 = this.graphicFactory.createBitmap(dimension.getWidth(), dimension.getHeight());
        } else {
            this.bitmap1 = null;
            this.bitmap2 = null;
        }
    }
}
