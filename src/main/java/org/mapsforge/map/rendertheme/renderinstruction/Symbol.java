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
 * Represents an icon on the map.
 */
public class Symbol implements RenderInstruction {
	private final Bitmap bitmap;

	public Symbol(final SymbolBuilder symbolBuilder) {
		this.bitmap = symbolBuilder.getBitmap();
	}

	@Override
	public void renderNode(final RenderCallback renderCallback, final List<Tag> tags) {
		renderCallback.renderPointOfInterestSymbol(this.bitmap);
	}

	@Override
	public void renderWay(final RenderCallback renderCallback, final List<Tag> tags) {
		renderCallback.renderAreaSymbol(this.bitmap);
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
