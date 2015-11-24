/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import java.io.IOException;

import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link Area} instances.
 */
public class AreaBuilder {
    private static final String FILL = "fill";
    private static final String SRC = "src";
    private static final String STROKE = "stroke";
    private static final String STROKE_WIDTH = "stroke-width";

    private final Paint fill;
    private final int level;
    private final Paint stroke;
    private float strokeWidth;

    public AreaBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final int level, final String relativePathPrefix) throws IOException, SAXException {
        this.level = level;

        this.fill = graphicFactory.createPaint();
        this.fill.setColor(graphicFactory.createColor(255, 0, 0, 0));
        this.fill.setStyle(Style.FILL);
        this.fill.setStrokeCap(Cap.ROUND);

        this.stroke = graphicFactory.createPaint();
        this.stroke.setColor(graphicFactory.createColor(0, 0, 0, 0));
        this.stroke.setStyle(Style.STROKE);
        this.stroke.setStrokeCap(Cap.ROUND);

        extractValues(graphicFactory, elementName, attributes, relativePathPrefix);
    }

    /**
     * @return a new {@code Area} instance.
     */
    public Area build() {
        return new Area(this);
    }

    private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (SRC.equals(name)) {
                this.fill.setBitmapShader(XmlUtils.createBitmap(graphicFactory, relativePathPrefix, value));
            } else if (FILL.equals(name)) {
                this.fill.setColor(graphicFactory.createColor(value));
            } else if (STROKE.equals(name)) {
                this.stroke.setColor(graphicFactory.createColor(value));
            } else if (STROKE_WIDTH.equals(name)) {
                this.strokeWidth = XmlUtils.parseNonNegativeFloat(name, value);
            } else {
                throw XmlUtils.createSAXException(elementName, name, value, i);
            }
        }
    }

    public Paint getFill() {
        return fill;
    }

    public int getLevel() {
        return level;
    }

    public Paint getStroke() {
        return stroke;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(final float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

}
