/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;

/**
 * Callback methods for rendering areas, ways and points of interest (POIs).
 */
public interface RenderCallback {
	/**
	 * Renders an area with the given parameters.
	 * 
	 * @param fill
	 *            the paint to be used for rendering the area.
	 * @param stroke
	 *            an optional paint for the area casing (may be null).
	 * @param level
	 *            the drawing level on which the area should be rendered.
	 */
	void renderArea(Paint fill, Paint stroke, int level);

	/**
	 * Renders an area caption with the given text.
	 * 
	 * @param caption
	 *            the text to be rendered.
	 * @param verticalOffset
	 *            the vertical offset of the caption.
	 * @param fill
	 *            the paint to be used for rendering the text.
	 * @param stroke
	 *            an optional paint for the text casing (may be null).
	 */
	void renderAreaCaption(String caption, float verticalOffset, Paint fill, Paint stroke);

	/**
	 * Renders an area symbol with the given bitmap.
	 * 
	 * @param symbol
	 *            the symbol to be rendered.
	 */
	void renderAreaSymbol(Bitmap symbol);

	/**
	 * Renders a point of interest caption with the given text.
	 * 
	 * @param caption
	 *            the text to be rendered.
	 * @param verticalOffset
	 *            the vertical offset of the caption.
	 * @param fill
	 *            the paint to be used for rendering the text.
	 * @param stroke
	 *            an optional paint for the text casing (may be null).
	 */
	void renderPointOfInterestCaption(String caption, float verticalOffset, Paint fill, Paint stroke);

	/**
	 * Renders a point of interest circle with the given parameters.
	 * 
	 * @param radius
	 *            the radius of the circle.
	 * @param fill
	 *            the paint to be used for rendering the circle.
	 * @param stroke
	 *            an optional paint for the circle casing (may be null).
	 * @param level
	 *            the drawing level on which the circle should be rendered.
	 */
	void renderPointOfInterestCircle(float radius, Paint fill, Paint stroke, int level);

	/**
	 * Renders a point of interest symbol with the given bitmap.
	 * 
	 * @param symbol
	 *            the symbol to be rendered.
	 */
	void renderPointOfInterestSymbol(Bitmap symbol);

	/**
	 * Renders a way with the given parameters.
	 * 
	 * @param stroke
	 *            the paint to be used for rendering the way.
	 * @param level
	 *            the drawing level on which the way should be rendered.
	 */
	void renderWay(Paint stroke, int level);

	/**
	 * Renders a way with the given symbol along the way path.
	 * 
	 * @param symbol
	 *            the symbol to be rendered.
	 * @param alignCenter
	 *            true if the symbol should be centered, false otherwise.
	 * @param repeat
	 *            true if the symbol should be repeated, false otherwise.
	 */
	void renderWaySymbol(Bitmap symbol, boolean alignCenter, boolean repeat);

	/**
	 * Renders a way with the given text along the way path.
	 * 
	 * @param text
	 *            the text to be rendered.
	 * @param fill
	 *            the paint to be used for rendering the text.
	 * @param stroke
	 *            an optional paint for the text casing (may be null).
	 */
	void renderWayText(String text, Paint fill, Paint stroke);
}
