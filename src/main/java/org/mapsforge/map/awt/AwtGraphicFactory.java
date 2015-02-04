/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.awt;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Path;

public final class AwtGraphicFactory implements GraphicFactory {
	public static final GraphicFactory INSTANCE = new AwtGraphicFactory();
	private static final java.awt.Color TRANSPARENT = new java.awt.Color(0, 0, 0, 0);

	public static Canvas createCanvas(final Graphics2D graphics2D) {
		return new AwtCanvas(graphics2D);
	}

	public static AffineTransform getAffineTransform(final Matrix matrix) {
		return ((AwtMatrix) matrix).getAffineTransform();
	}

	public static AwtPaint getAwtPaint(final Paint paint) {
		return (AwtPaint) paint;
	}

	public static BufferedImage getBufferedImage(final Bitmap bitmap) {
		return ((AwtBitmap) bitmap).getBufferedImage();
	}

	public static Path2D getPath(final Path path) {
		return ((AwtPath) path).getPath();
	}

	private AwtGraphicFactory() {
		// do nothing
	}

	@Override
	public Bitmap createBitmap(final InputStream inputStream) {
		try {
			final BufferedImage bufferedImage = ImageIO.read(inputStream);
			return new AwtBitmap(bufferedImage);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Bitmap createBitmap(final int width, final int height) {
		return new AwtBitmap(width, height);
	}

	@Override
	public Canvas createCanvas() {
		return new AwtCanvas();
	}

	@Override
	public int createColor(final Color color) {
		switch (color) {
		case BLACK:
			return java.awt.Color.BLACK.getRGB();
		case BLUE:
			return java.awt.Color.BLUE.getRGB();
		case GREEN:
			return java.awt.Color.GREEN.getRGB();
		case RED:
			return java.awt.Color.RED.getRGB();
		case TRANSPARENT:
			return TRANSPARENT.getRGB();
		case WHITE:
			return java.awt.Color.WHITE.getRGB();
		default:
			throw new IllegalArgumentException("unknown color: " + color);
		}
	}

	@Override
	public int createColor(final int alpha, final int red, final int green, final int blue) {
		return new java.awt.Color(alpha, red, green, blue).getRGB();
	}

	@Override
	public int createColor(final String colorString) {
		final long parseLong = Long.parseLong(colorString.substring(1), 16);
		return new java.awt.Color((int) parseLong, true).getRGB();
	}

	@Override
	public Matrix createMatrix() {
		return new AwtMatrix();
	}

	@Override
	public Paint createPaint() {
		return new AwtPaint();
	}

	@Override
	public Path createPath() {
		return new AwtPath();
	}
}
