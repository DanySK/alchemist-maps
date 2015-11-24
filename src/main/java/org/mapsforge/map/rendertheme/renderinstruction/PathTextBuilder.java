/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import java.util.Locale;

import org.mapsforge.core.graphics.Align;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link PathText} instances.
 */
public class PathTextBuilder {
    private static final String FILL = "fill";
    private static final String FONT_FAMILY = "font-family";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_STYLE = "font-style";
    private static final String K = "k";
    private static final String STROKE = "stroke";
    private static final String STROKE_WIDTH = "stroke-width";

    private final Paint fill;
    private float fontSize;
    private final Paint stroke;
    private TextKey textKey;

    public PathTextBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes) throws SAXException {
        this.fill = graphicFactory.createPaint();
        this.fill.setColor(graphicFactory.createColor(Color.BLACK));
        this.fill.setStyle(Style.FILL);
        this.fill.setTextAlign(Align.CENTER);

        this.stroke = graphicFactory.createPaint();
        this.stroke.setColor(graphicFactory.createColor(Color.BLACK));
        this.stroke.setStyle(Style.STROKE);
        this.stroke.setTextAlign(Align.CENTER);

        extractValues(graphicFactory, elementName, attributes);
    }

    /**
     * @return a new {@code PathText} instance.
     */
    public PathText build() {
        return new PathText(this);
    }

    private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes) throws SAXException {
        FontFamily fontFamily = FontFamily.DEFAULT;
        FontStyle fontStyle = FontStyle.NORMAL;

        for (int i = 0; i < attributes.getLength(); ++i) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (K.equals(name)) {
                this.textKey = TextKey.getInstance(value);
            } else if (FONT_FAMILY.equals(name)) {
                fontFamily = FontFamily.valueOf(value.toUpperCase(Locale.ENGLISH));
            } else if (FONT_STYLE.equals(name)) {
                fontStyle = FontStyle.valueOf(value.toUpperCase(Locale.ENGLISH));
            } else if (FONT_SIZE.equals(name)) {
                this.fontSize = XmlUtils.parseNonNegativeFloat(name, value);
            } else if (FILL.equals(name)) {
                this.fill.setColor(graphicFactory.createColor(value));
            } else if (STROKE.equals(name)) {
                this.stroke.setColor(graphicFactory.createColor(value));
            } else if (STROKE_WIDTH.equals(name)) {
                this.stroke.setStrokeWidth(XmlUtils.parseNonNegativeFloat(name, value));
            } else {
                throw XmlUtils.createSAXException(elementName, name, value, i);
            }
        }

        this.fill.setTypeface(fontFamily, fontStyle);
        this.stroke.setTypeface(fontFamily, fontStyle);

        XmlUtils.checkMandatoryAttribute(elementName, K, this.textKey);
    }

    public Paint getFill() {
        return fill;
    }

    public float getFontSize() {
        return fontSize;
    }

    public Paint getStroke() {
        return stroke;
    }

    public TextKey getTextKey() {
        return textKey;
    }

    public void setFontSize(final float fontSize) {
        this.fontSize = fontSize;
    }

    public void setTextKey(final TextKey textKey) {
        this.textKey = textKey;
    }
}
