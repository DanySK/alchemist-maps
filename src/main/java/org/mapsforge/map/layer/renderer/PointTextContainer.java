/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.Rectangle;

class PointTextContainer {
    private final Rectangle boundary;
    private final Paint paintBack;
    private final Paint paintFront;
    private SymbolContainer symbol;
    private final String text;
    private double x;

    private double y;

    /**
     * Create a new point container, that holds the x-y coordinates of a point,
     * a text variable and one paint objects.
     * 
     * @param text
     *            the text of the point.
     * @param x
     *            the x coordinate of the point.
     * @param y
     *            the y coordinate of the point.
     * @param paintFront
     *            the paintFront for the point.
     */
    public PointTextContainer(final String text, final double x, final double y, final Paint paintFront) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paintFront = paintFront;
        this.paintBack = null;

        this.boundary = new Rectangle(0, 0, paintFront.getTextWidth(text), paintFront.getTextHeight(text));
    }

    /**
     * Create a new point container, that holds the x-y coordinates of a point,
     * a text variable and two paint objects.
     * 
     * @param text
     *            the text of the point.
     * @param x
     *            the x coordinate of the point.
     * @param y
     *            the y coordinate of the point.
     * @param paintFront
     *            the paintFront for the point.
     * @param paintBack
     *            the paintBack for the point.
     */
    public PointTextContainer(final String text, final double x, final double y, final Paint paintFront, final Paint paintBack) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paintFront = paintFront;
        this.paintBack = paintBack;

        if (paintBack != null) {
            paintBack.getTextHeight(text);
            paintBack.getTextWidth(text);
            this.boundary = new Rectangle(0, 0, paintBack.getTextWidth(text), paintBack.getTextHeight(text));
        } else {
            this.boundary = new Rectangle(0, 0, paintFront.getTextWidth(text), paintFront.getTextHeight(text));
        }
    }

    /**
     * Create a new point container, that holds the x-y coordinates of a point,
     * a text variable, two paint objects, and a reference on a symbol, if the
     * text is connected with a POI.
     * 
     * @param text
     *            the text of the point.
     * @param x
     *            the x coordinate of the point.
     * @param y
     *            the y coordinate of the point.
     * @param paintFront
     *            the paintFront for the point.
     * @param paintBack
     *            the paintBack for the point.
     * @param symbol
     *            the connected Symbol.
     */
    public PointTextContainer(final String text, final double x, final double y, final Paint paintFront, final Paint paintBack, final SymbolContainer symbol) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paintFront = paintFront;
        this.paintBack = paintBack;
        this.symbol = symbol;

        if (paintBack != null) {
            this.boundary = new Rectangle(0, 0, paintBack.getTextWidth(text), paintBack.getTextHeight(text));
        } else {
            this.boundary = new Rectangle(0, 0, paintFront.getTextWidth(text), paintFront.getTextHeight(text));
        }
    }

    public Rectangle getBoundary() {
        return boundary;
    }

    public Paint getPaintBack() {
        return paintBack;
    }

    public Paint getPaintFront() {
        return paintFront;
    }

    public SymbolContainer getSymbol() {
        return symbol;
    }

    public String getText() {
        return text;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setSymbol(final SymbolContainer symbol) {
        this.symbol = symbol;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public void setY(final double y) {
        this.y = y;
    }
}
