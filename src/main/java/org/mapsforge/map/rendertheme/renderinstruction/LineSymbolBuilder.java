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

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link LineSymbol} instances.
 */
public class LineSymbolBuilder {
    private static final String ALIGN_CENTER = "align-center";
    private static final String REPEAT = "repeat";
    private static final String SRC = "src";

    private boolean alignCenter;
    private Bitmap bitmap;
    private boolean repeat;

    public LineSymbolBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
        extractValues(graphicFactory, elementName, attributes, relativePathPrefix);
    }

    /**
     * @return a new {@code LineSymbol} instance.
     */
    public LineSymbol build() {
        return new LineSymbol(this);
    }

    private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (SRC.equals(name)) {
                this.bitmap = XmlUtils.createBitmap(graphicFactory, relativePathPrefix, value);
            } else if (ALIGN_CENTER.equals(name)) {
                this.alignCenter = Boolean.parseBoolean(value);
            } else if (REPEAT.equals(name)) {
                this.repeat = Boolean.parseBoolean(value);
            } else {
                throw XmlUtils.createSAXException(elementName, name, value, i);
            }
        }

        XmlUtils.checkMandatoryAttribute(elementName, SRC, this.bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isAlignCenter() {
        return alignCenter;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setAlignCenter(final boolean alignCenter) {
        this.alignCenter = alignCenter;
    }

    public void setBitmap(final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setRepeat(final boolean repeat) {
        this.repeat = repeat;
    }
}
