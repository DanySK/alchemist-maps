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

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.Tag;
import org.mapsforge.map.rendertheme.RenderCallback;

/**
 * Represents a text along a polyline on the map.
 */
public class PathText implements RenderInstruction {
    private final Paint fill;
    private final float fontSize;
    private final Paint stroke;
    private final TextKey textKey;

    public PathText(final PathTextBuilder pathTextBuilder) {
        this.fill = pathTextBuilder.getFill();
        this.fontSize = pathTextBuilder.getFontSize();
        this.stroke = pathTextBuilder.getStroke();
        this.textKey = pathTextBuilder.getTextKey();
    }

    @Override
    public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
        // do nothing
    }

    @Override
    public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
        final String caption = this.textKey.getValue(tags);
        if (caption == null) {
            return;
        }
        renderCallback.renderWayText(caption, this.fill, this.stroke);
    }

    @Override
    public void scaleStrokeWidth(final float scaleFactor) {
        // do nothing
    }

    @Override
    public void scaleTextSize(final float scaleFactor) {
        this.fill.setTextSize(this.fontSize * scaleFactor);
        this.stroke.setTextSize(this.fontSize * scaleFactor);
    }
}
