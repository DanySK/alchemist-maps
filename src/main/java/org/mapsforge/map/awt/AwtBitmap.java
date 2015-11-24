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
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.mapsforge.core.graphics.Bitmap;

public class AwtBitmap implements Bitmap {
    private final BufferedImage bufferedImage;

    AwtBitmap(final BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    AwtBitmap(final int width, final int height) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void copyPixelsFromBuffer(final ByteBuffer byteBuffer) {
        final int[] pixels = new int[byteBuffer.array().length / 4];
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = byteBuffer.getInt();
        }

        this.bufferedImage.setRGB(0, 0, getWidth(), getHeight(), pixels, 0, getWidth());
    }

    @Override
    public void copyPixelsToBuffer(final ByteBuffer byteBuffer) {
        final int[] pixels = this.bufferedImage.getRGB(0, 0, getWidth(), getHeight(), null, 0, getWidth());
        for (final int pixel : pixels) {
            byteBuffer.putInt(pixel);
        }
    }

    @Override
    public void fillColor(final int color) {
        final Graphics2D graphics2D = this.bufferedImage.createGraphics();
        graphics2D.setColor(new java.awt.Color(color));
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    @Override
    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    @Override
    public int getWidth() {
        return this.bufferedImage.getWidth();
    }
}
