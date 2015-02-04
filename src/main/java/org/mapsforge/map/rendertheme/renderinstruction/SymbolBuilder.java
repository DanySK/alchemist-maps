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
 * A builder for {@link Symbol} instances.
 */
public class SymbolBuilder {
	private static final String SRC = "src";

	private Bitmap bitmap;

	public SymbolBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
		extractValues(graphicFactory, elementName, attributes, relativePathPrefix);
	}

	/**
	 * @return a new {@code Symbol} instance.
	 */
	public Symbol build() {
		return new Symbol(this);
	}

	private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
		for (int i = 0; i < attributes.getLength(); ++i) {
			final String name = attributes.getQName(i);
			final String value = attributes.getValue(i);

			if (SRC.equals(name)) {
				this.bitmap = XmlUtils.createBitmap(graphicFactory, relativePathPrefix, value);
			} else {
				throw XmlUtils.createSAXException(elementName, name, value, i);
			}
		}

		XmlUtils.checkMandatoryAttribute(elementName, SRC, this.bitmap);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(final Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
