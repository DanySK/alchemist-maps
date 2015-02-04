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

import org.mapsforge.core.model.Tag;
import org.mapsforge.map.rendertheme.RenderCallback;

/**
 * A RenderInstruction is a basic graphical primitive to draw a map.
 */
public interface RenderInstruction {
	/**
	 * @param renderCallback
	 *            a reference to the receiver of all render callbacks.
	 * @param tags
	 *            the tags of the node.
	 */
	void renderNode(RenderCallback renderCallback, List<Tag> tags);

	/**
	 * @param renderCallback
	 *            a reference to the receiver of all render callbacks.
	 * @param tags
	 *            the tags of the way.
	 */
	void renderWay(RenderCallback renderCallback, List<Tag> tags);

	/**
	 * Scales the stroke width of this RenderInstruction by the given factor.
	 * 
	 * @param scaleFactor
	 *            the factor by which the stroke width should be scaled.
	 */
	void scaleStrokeWidth(float scaleFactor);

	/**
	 * Scales the text size of this RenderInstruction by the given factor.
	 * 
	 * @param scaleFactor
	 *            the factor by which the text size should be scaled.
	 */
	void scaleTextSize(float scaleFactor);
}
