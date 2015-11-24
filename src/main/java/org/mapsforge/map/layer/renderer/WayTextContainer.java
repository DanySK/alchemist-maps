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

public class WayTextContainer {
    private final Paint paint;
    private final String text;
    private final int x1;
    private final int x2;
    private final int y1;
    private final int y2;

    public WayTextContainer(final int x1, final int y1, final int x2, final int y2, final String text, final Paint paint) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.text = text;
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public String getText() {
        return text;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }
}
