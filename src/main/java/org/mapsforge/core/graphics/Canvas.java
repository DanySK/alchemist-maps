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
 * 
 */
public interface Canvas {
    /**
     * @param bitmap
     *            bitmap
     * @param left
     *            left
     * @param top
     *            top
     */
    void drawBitmap(Bitmap bitmap, int left, int top);

    /**
     * @param bitmap
     *            bitmap
     * @param matrix
     *            matrix
     */
    void drawBitmap(Bitmap bitmap, Matrix matrix);

    /**
     * @param x
     *            x
     * @param y
     *            y
     * @param radius
     *            radius
     * @param paint
     *            paint
     */
    void drawCircle(int x, int y, int radius, Paint paint);

    /**
     * @param x1
     *            x1
     * @param y1
     *            y1
     * @param x2
     *            x2
     * @param y2
     *            y2
     * @param paint
     *            paint
     */
    void drawLine(int x1, int y1, int x2, int y2, Paint paint);

    /**
     * @param path
     *            path
     * @param paint
     *            paint
     */
    void drawPath(Path path, Paint paint);

    /**
     * @param text
     *            text
     * @param x
     *            x
     * @param y
     *            y
     * @param paint
     *            paint
     */
    void drawText(String text, int x, int y, Paint paint);

    /**
     * @param text
     *            text
     * @param x1
     *            x1
     * @param y1
     *            y1
     * @param x2
     *            x2
     * @param y2
     *            y2
     * @param paint
     *            paint
     */
    void drawTextRotated(String text, int x1, int y1, int x2, int y2, Paint paint);

    /**
     * @param color
     *            color
     */
    void fillColor(int color);

    /**
     * @return height
     */
    int getHeight();

    /**
     * @return width
     */
    int getWidth();

    /**
     * @param bitmap
     *            bitmap
     */
    void setBitmap(Bitmap bitmap);
}
