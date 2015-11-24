/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link RenderTheme} instances.
 */
public class RenderThemeBuilder {
    private static final String BASE_STROKE_WIDTH = "base-stroke-width";
    private static final String BASE_TEXT_SIZE = "base-text-size";
    private static final String MAP_BACKGROUND = "map-background";
    private static final int RENDER_THEME_VERSION = 2;
    private static final String VERSION = "version";
    private static final String XMLNS = "xmlns";
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_SCHEMALOCATION = "xsi:schemaLocation";

    private Integer version;
    private float baseStrokeWidth;
    private float baseTextSize;
    private int mapBackground;

    public RenderThemeBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes) throws SAXException {
        this.baseStrokeWidth = 1;
        this.baseTextSize = 1;
        this.mapBackground = graphicFactory.createColor(Color.WHITE);

        extractValues(graphicFactory, elementName, attributes);
    }

    /**
     * @return a new {@code RenderTheme} instance.
     */
    public RenderTheme build() {
        return new RenderTheme(this);
    }

    private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (XMLNS.equals(name)) {
                continue;
            } else if (XMLNS_XSI.equals(name)) {
                continue;
            } else if (XSI_SCHEMALOCATION.equals(name)) {
                continue;
            } else if (VERSION.equals(name)) {
                this.version = Integer.valueOf(XmlUtils.parseNonNegativeInteger(name, value));
            } else if (MAP_BACKGROUND.equals(name)) {
                this.mapBackground = graphicFactory.createColor(value);
            } else if (BASE_STROKE_WIDTH.equals(name)) {
                this.baseStrokeWidth = XmlUtils.parseNonNegativeFloat(name, value);
            } else if (BASE_TEXT_SIZE.equals(name)) {
                this.baseTextSize = XmlUtils.parseNonNegativeFloat(name, value);
            } else {
                throw XmlUtils.createSAXException(elementName, name, value, i);
            }
        }

        validate(elementName);
    }

    public float getBaseStrokeWidth() {
        return baseStrokeWidth;
    }

    public float getBaseTextSize() {
        return baseTextSize;
    }

    public int getMapBackground() {
        return mapBackground;
    }

    public void setBaseStrokeWidth(final float baseStrokeWidth) {
        this.baseStrokeWidth = baseStrokeWidth;
    }

    public void setBaseTextSize(final float baseTextSize) {
        this.baseTextSize = baseTextSize;
    }

    public void setMapBackground(final int mapBackground) {
        this.mapBackground = mapBackground;
    }

    private void validate(final String elementName) throws SAXException {
        XmlUtils.checkMandatoryAttribute(elementName, VERSION, this.version);

        if (this.version.intValue() != RENDER_THEME_VERSION) {
            throw new SAXException("unsupported render theme version: " + this.version);
        }
    }
}
