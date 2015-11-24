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
 * Represents a closed polygon on the map.
 */
public class Area implements RenderInstruction {
    private final Paint fill;
    private final int level;
    private final Paint stroke;
    private final float strokeWidth;

    public Area(final AreaBuilder areaBuilder) {
        this.fill = areaBuilder.getFill();
        this.level = areaBuilder.getLevel();
        this.stroke = areaBuilder.getStroke();
        this.strokeWidth = areaBuilder.getStrokeWidth();
    }

    @Override
    public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
        // do nothing
    }

    @Override
    public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
        renderCallback.renderArea(this.fill, this.stroke, this.level);
    }

    @Override
    public void scaleStrokeWidth(final float scaleFactor) {
        if (this.stroke != null) {
            this.stroke.setStrokeWidth(this.strokeWidth * scaleFactor);
        }
    }

    @Override
    public void scaleTextSize(final float scaleFactor) {
        // do nothing
    }
}
