/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.graphics.Paint;

public class ShapePaintContainer {
    private final Paint paint;
    private final ShapeContainer shapeContainer;

    public ShapePaintContainer(final ShapeContainer shapeContainer, final Paint paint) {
        this.shapeContainer = shapeContainer;
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public ShapeContainer getShapeContainer() {
        return shapeContainer;
    }

}
