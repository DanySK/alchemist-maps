/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.graphics;

/**
 * @author Mapsforge
 * 
 */
public interface Paint {

	/**
	 * @return color
	 */
	int getColor();

	/**
	 * @return StrokeCap
	 */
	Cap getStrokeCap();

	/**
	 * @return StrokeWidth
	 */
	float getStrokeWidth();

	/**
	 * @param text
	 *            text
	 * @return TextHeight
	 */
	int getTextHeight(String text);

	/**
	 * @param text
	 *            text
	 * @return TextWidth
	 */
	int getTextWidth(String text);

	/**
	 * @param alpha
	 *            alpha
	 */
	void setAlpha(int alpha);

	/**
	 * @param bitmap
	 *            bitmap
	 */
	void setBitmapShader(Bitmap bitmap);

	/**
	 * @param color
	 *            color
	 */
	void setColor(int color);

	/**
	 * @param strokeDasharray
	 *            strokeDasharray
	 */
	void setDashPathEffect(float[] strokeDasharray);

	/**
	 * @param cap
	 *            cap
	 */
	void setStrokeCap(Cap cap);

	/**
	 * @param strokeWidth
	 *            strokeWidth
	 */
	void setStrokeWidth(float strokeWidth);

	/**
	 * @param style
	 *            style
	 */
	void setStyle(Style style);

	/**
	 * @param align
	 *            align
	 */
	void setTextAlign(Align align);

	/**
	 * @param textSize
	 *            textSize
	 */
	void setTextSize(float textSize);

	/**
	 * @param fontFamily
	 *            fontFamily
	 * @param fontStyle
	 *            fontStyle
	 */
	void setTypeface(FontFamily fontFamily, FontStyle fontStyle);
}
