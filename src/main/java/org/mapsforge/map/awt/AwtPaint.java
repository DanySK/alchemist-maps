/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.awt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import org.mapsforge.core.graphics.Align;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;

// TODO check default values
class AwtPaint implements Paint {

	private Align align = Align.LEFT;
	private Cap cap = Cap.BUTT;
	private int color = 0;
	private String fontName;
	private int fontStyle;
	private float[] strokeDasharray;
	private float strokeWidth;
	private float textSize;
	private Bitmap bitmap;

	private Font font;

	private Style style = Style.FILL;

	private static String getFontName(final FontFamily fontFamily) {
		switch (fontFamily) {
		case MONOSPACE:
			return Font.MONOSPACED;

		case DEFAULT:
			return null;

		case SANS_SERIF:
			return Font.SANS_SERIF;

		case SERIF:
			return Font.SERIF;

		default:
			throw new IllegalArgumentException("unknown fontFamily: " + fontFamily);

		}

	}

	private static int getFontStyle(final FontStyle fontStyle) {
		switch (fontStyle) {
		case BOLD:
			return Font.BOLD;

		case BOLD_ITALIC:
			return Font.BOLD | Font.ITALIC;

		case ITALIC:
			return Font.ITALIC;

		case NORMAL:
			return Font.PLAIN;

		default:
			throw new IllegalArgumentException("unknown fontStyle: " + fontStyle);
		}

	}

	private void createFont() {
		if (this.textSize > 0) {
			this.font = new Font(this.fontName, this.fontStyle, (int) this.textSize);
		} else {
			this.font = null;
		}
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public int getColor() {
		return this.color;
	}

	public Font getFont() {
		return font;
	}

	@Override
	public Cap getStrokeCap() {
		return this.cap;
	}

	@Override
	public float getStrokeWidth() {
		return this.strokeWidth;
	}

	public Style getStyle() {
		return style;
	}

	@Override
	public int getTextHeight(final String text) {
		final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(this.font);
		return fontMetrics.getHeight();
	}

	@Override
	public int getTextWidth(final String text) {
		final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(this.font);
		return fontMetrics.stringWidth(text);
	}

	@Override
	public void setAlpha(final int alpha) {
		throw new UnsupportedOperationException();
	}

	public void setBitmap(final Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public void setBitmapShader(final Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public void setColor(final int color) {
		this.color = color;
	}

	@Override
	public void setDashPathEffect(final float[] strokeDasharray) {
		this.strokeDasharray = strokeDasharray;
	}

	public void setFont(final Font font) {
		this.font = font;
	}

	@Override
	public void setStrokeCap(final Cap cap) {
		this.cap = cap;
	}

	@Override
	public void setStrokeWidth(final float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@Override
	public void setStyle(final Style style) {
		this.style = style;
	}

	@Override
	public void setTextAlign(final Align align) {
		this.align = align;
	}

	@Override
	public void setTextSize(final float textSize) {
		this.textSize = textSize;
		createFont();
	}

	@Override
	public void setTypeface(final FontFamily fontFamily, final FontStyle fontStyle) {
		this.fontName = getFontName(fontFamily);
		this.fontStyle = getFontStyle(fontStyle);
		createFont();
	}
}
