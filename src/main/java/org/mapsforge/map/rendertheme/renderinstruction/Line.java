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
 * Represents a polyline on the map.
 */
public class Line implements RenderInstruction {
	private final int level;
	private final Paint stroke;
	private final float strokeWidth;

	public Line(final LineBuilder lineBuilder) {
		this.level = lineBuilder.getLevel();
		this.stroke = lineBuilder.getStroke();
		this.strokeWidth = lineBuilder.getStrokeWidth();
	}

	@Override
	public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
		// do nothing
	}

	@Override
	public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
		renderCallback.renderWay(this.stroke, this.level);
	}

	@Override
	public void scaleStrokeWidth(final float scaleFactor) {
		this.stroke.setStrokeWidth(this.strokeWidth * scaleFactor);
	}

	@Override
	public void scaleTextSize(final float scaleFactor) {
		// do nothing
	}
}
