/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.graphics;

import java.nio.ByteBuffer;

/**
 * 
 */
public interface Bitmap {
    /**
     * @param byteBuffer
     *            byteBuffer
     */
    void copyPixelsFromBuffer(ByteBuffer byteBuffer);

    /**
     * @param byteBuffer
     *            byteBuffer
     */
    void copyPixelsToBuffer(ByteBuffer byteBuffer);

    /**
     * @param color
     *            color
     */
    void fillColor(int color);

    /**
     * @return the height of this bitmap in pixels.
     */
    int getHeight();

    /**
     * @return the width of this bitmap in pixels.
     */
    int getWidth();
}
