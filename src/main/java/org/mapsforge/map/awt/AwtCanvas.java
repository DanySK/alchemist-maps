/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.awt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Path;
import org.mapsforge.core.graphics.Style;

public class AwtCanvas implements Canvas {
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;

    private static int getCap(final Cap cap) {
        switch (cap) {
        case BUTT:
            return BasicStroke.CAP_BUTT;
        case ROUND:
            return BasicStroke.CAP_ROUND;
        case SQUARE:
            return BasicStroke.CAP_SQUARE;
        default:
            throw new IllegalArgumentException("unknown cap: " + cap);
        }
    }

    private static Stroke getStroke(final Paint paint) {
        final int cap = getCap(paint.getStrokeCap());
        return new BasicStroke(paint.getStrokeWidth(), cap, BasicStroke.JOIN_ROUND);
    }

    protected AwtCanvas() {
    }

    protected AwtCanvas(final Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    @Override
    public void drawBitmap(final Bitmap bitmap, final int left, final int top) {
        this.graphics2D.drawImage(AwtGraphicFactory.getBufferedImage(bitmap), left, top, null);
    }

    @Override
    public void drawBitmap(final Bitmap bitmap, final Matrix matrix) {
        this.graphics2D.drawRenderedImage(AwtGraphicFactory.getBufferedImage(bitmap), AwtGraphicFactory.getAffineTransform(matrix));
    }

    @Override
    public void drawCircle(final int x, final int y, final int radius, final Paint paint) {
        setPaintAttributes(paint);
        // FIXME if circles aren't well centered remove "- radious / 2"
        this.graphics2D.drawOval(x - radius / 2, y - radius / 2, radius, radius);
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2, final Paint paint) {
        setPaintAttributes(paint);
        this.graphics2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawPath(final Path path, final Paint paint) {
        // FIXME do not send empty paths to the canvas
        if (path.isEmpty()) {
            return;
        }
        setPaintAttributes(paint);

        final AwtPaint awtPaint = AwtGraphicFactory.getAwtPaint(paint);
        final Bitmap bitmap = awtPaint.getBitmap();
        if (bitmap != null) {
            final Rectangle rectangle = new Rectangle(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final TexturePaint texturePaint = new TexturePaint(AwtGraphicFactory.getBufferedImage(bitmap), rectangle);
            this.graphics2D.setPaint(texturePaint);
        }

        final Style style = awtPaint.getStyle();
        final Path2D p = AwtGraphicFactory.getPath(path);
        if (style.equals(Style.FILL)) {
            this.graphics2D.fill(p);
        } else if (style.equals(Style.STROKE)) {
            this.graphics2D.draw(p);
        }
        throw new IllegalArgumentException("unknown style: " + style);
    }

    @Override
    public void drawText(final String text, final int x, final int y, final Paint paint) {
        setPaintAttributes(paint);
        this.graphics2D.setFont(AwtGraphicFactory.getAwtPaint(paint).getFont());
        this.graphics2D.drawString(text, x, y);
    }

    @Override
    public void drawTextRotated(final String text, final int x1, final int y1, final int x2, final int y2, final Paint paint) {
        final double theta = Math.atan2(y1 - y2, x1 - x2);
        final AffineTransform affineTransform = this.graphics2D.getTransform();
        this.graphics2D.rotate(theta, x1, y1);
        drawText(text, x1, y1, paint);
        this.graphics2D.setTransform(affineTransform);
    }

    @Override
    public void fillColor(final int color) {
        this.graphics2D.setColor(new java.awt.Color(color));
        this.graphics2D.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    @Override
    public int getWidth() {
        return this.bufferedImage.getWidth();
    }

    @Override
    public void setBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            this.bufferedImage = null;
            this.graphics2D = null;
        } else {
            this.bufferedImage = AwtGraphicFactory.getBufferedImage(bitmap);
            this.graphics2D = this.bufferedImage.createGraphics();
            this.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
    }

    private void setPaintAttributes(final Paint paint) {
        this.graphics2D.setColor(new java.awt.Color(paint.getColor()));
        this.graphics2D.setStroke(getStroke(paint));
    }
}
