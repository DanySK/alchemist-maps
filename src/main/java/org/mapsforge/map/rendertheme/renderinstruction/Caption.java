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
 * Represents a text label on the map.
 */
public class Caption implements RenderInstruction {
	private final float dy;
	private final Paint fill;
	private final float fontSize;
	private final Paint stroke;
	private final TextKey textKey;

	public Caption(final CaptionBuilder captionBuilder) {
		this.dy = captionBuilder.getDy();
		this.fill = captionBuilder.getFill();
		this.fontSize = captionBuilder.getFontSize();
		this.stroke = captionBuilder.getStroke();
		this.textKey = captionBuilder.getTextKey();
	}

	@Override
	public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
		final String caption = this.textKey.getValue(tags);
		if (caption == null) {
			return;
		}
		renderCallback.renderPointOfInterestCaption(caption, this.dy, this.fill, this.stroke);
	}

	@Override
	public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
		final String caption = this.textKey.getValue(tags);
		if (caption == null) {
			return;
		}
		renderCallback.renderAreaCaption(caption, this.dy, this.fill, this.stroke);
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
