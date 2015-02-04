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
 * Represents a round area on the map.
 */
public class Circle implements RenderInstruction {
	private final Paint fill;
	private final int level;
	private final float radius;
	private float renderRadius;
	private final boolean scaleRadius;
	private final Paint stroke;
	private final float strokeWidth;

	public Circle(final CircleBuilder circleBuilder) {
		this.fill = circleBuilder.getFill();
		this.level = circleBuilder.getLevel();
		this.radius = circleBuilder.getRadius().floatValue();
		this.scaleRadius = circleBuilder.isScaleRadius();
		this.stroke = circleBuilder.getStroke();
		this.strokeWidth = circleBuilder.getStrokeWidth();

		if (!this.scaleRadius) {
			this.renderRadius = this.radius;
			if (this.stroke != null) {
				this.stroke.setStrokeWidth(this.strokeWidth);
			}
		}
	}

	@Override
	public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
		renderCallback.renderPointOfInterestCircle(this.renderRadius, this.fill, this.stroke, this.level);
	}

	@Override
	public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
		// do nothing
	}

	@Override
	public void scaleStrokeWidth(final float scaleFactor) {
		if (this.scaleRadius) {
			this.renderRadius = this.radius * scaleFactor;
			if (this.stroke != null) {
				this.stroke.setStrokeWidth(this.strokeWidth * scaleFactor);
			}
		}
	}

	@Override
	public void scaleTextSize(final float scaleFactor) {
		// do nothing
	}
}
