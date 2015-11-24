/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.Tag;
import org.mapsforge.map.rendertheme.RenderCallback;

/**
 * Represents an icon along a polyline on the map.
 */
public class LineSymbol implements RenderInstruction {
    private final boolean alignCenter;
    private final Bitmap bitmap;
    private final boolean repeat;

    LineSymbol(final LineSymbolBuilder lineSymbolBuilder) {
        this.alignCenter = lineSymbolBuilder.isAlignCenter();
        this.bitmap = lineSymbolBuilder.getBitmap();
        this.repeat = lineSymbolBuilder.isRepeat();
    }

    @Override
    public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
        // do nothing
    }

    @Override
    public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
        renderCallback.renderWaySymbol(this.bitmap, this.alignCenter, this.repeat);
    }

    @Override
    public void scaleStrokeWidth(final float scaleFactor) {
        // do nothing
    }

    @Override
    public void scaleTextSize(final float scaleFactor) {
        // do nothing
    }
}
