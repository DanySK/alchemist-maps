/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;

public abstract class Layer {
    private boolean visible = true;

    public void destroy() {
        // do nothing
    }

    /**
     * Draws this {@code Layer} on the given canvas.
     * 
     * @param boundingBox
     *            the geographical area which should be drawn.
     * @param zoomLevel
     *            the zoom level at which this {@code Layer} should draw itself.
     * @param canvas
     *            the canvas on which this {@code Layer} should draw itself.
     * @param canvasPosition
     *            the top-left pixel position of the canvas relative to the
     *            top-left map position.
     */
    public abstract void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point canvasPosition);

    /**
     * @return true if this {@code Layer} is currently visible, false otherwise.
     *         The default value is true.
     */
    public final boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets the visibility flag of this {@code Layer} to the given value.
     */
    public final void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
