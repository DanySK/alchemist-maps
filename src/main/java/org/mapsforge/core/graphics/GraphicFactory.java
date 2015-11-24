/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.graphics;

import java.io.InputStream;

/**
 * 
 */
public interface GraphicFactory {
    /**
     * @param inputStream
     *            inputStream
     * @return Bitmap
     */
    Bitmap createBitmap(InputStream inputStream);

    /**
     * @param width
     *            width
     * @param height
     *            height
     * @return bitmap
     */
    Bitmap createBitmap(int width, int height);

    /**
     * @return canvas
     */
    Canvas createCanvas();

    /**
     * @param color
     *            color
     * @return color
     */
    int createColor(Color color);

    /**
     * @param alpha
     *            alpha
     * @param red
     *            red
     * @param green
     *            green
     * @param blue
     *            blue
     * @return color
     */
    int createColor(int alpha, int red, int green, int blue);

    /**
     * Supported formats are {@code #RRGGBB} and {@code #AARRGGBB}.
     * 
     * @param colorString
     *            colorString
     * @return color
     */
    int createColor(String colorString);

    /**
     * @return matrix
     */
    Matrix createMatrix();

    /**
     * @return paint
     */
    Paint createPaint();

    /**
     * @return path
     */
    Path createPath();
}
