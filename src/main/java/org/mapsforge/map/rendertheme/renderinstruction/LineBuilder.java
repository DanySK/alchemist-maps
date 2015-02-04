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
import java.util.Locale;
import java.util.regex.Pattern;

import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link Line} instances.
 */
public class LineBuilder {
	private static final Pattern SPLIT_PATTERN = Pattern.compile(",");
	private static final String SRC = "src";
	private static final String STROKE = "stroke";
	private static final String STROKE_DASHARRAY = "stroke-dasharray";
	private static final String STROKE_LINECAP = "stroke-linecap";
	private static final String STROKE_WIDTH = "stroke-width";

	private final int level;

	private final Paint stroke;
	private float strokeWidth;

	private static float[] parseFloatArray(final String name, final String dashString) throws SAXException {
		final String[] dashEntries = SPLIT_PATTERN.split(dashString);
		final float[] dashIntervals = new float[dashEntries.length];
		for (int i = 0; i < dashEntries.length; ++i) {
			dashIntervals[i] = XmlUtils.parseNonNegativeFloat(name, dashEntries[i]);
		}
		return dashIntervals;
	}

	public LineBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final int level, final String relativePathPrefix) throws IOException, SAXException {
		this.level = level;

		this.stroke = graphicFactory.createPaint();
		this.stroke.setColor(graphicFactory.createColor(Color.BLACK));
		this.stroke.setStyle(Style.STROKE);
		this.stroke.setStrokeCap(Cap.ROUND);

		extractValues(graphicFactory, elementName, attributes, relativePathPrefix);
	}

	/**
	 * @return a new {@code Line} instance.
	 */
	public Line build() {
		return new Line(this);
	}

	private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final String relativePathPrefix) throws IOException, SAXException {
		for (int i = 0; i < attributes.getLength(); ++i) {
			final String name = attributes.getQName(i);
			final String value = attributes.getValue(i);

			if (SRC.equals(name)) {
				this.stroke.setBitmapShader(XmlUtils.createBitmap(graphicFactory, relativePathPrefix, value));
			} else if (STROKE.equals(name)) {
				this.stroke.setColor(graphicFactory.createColor(value));
			} else if (STROKE_WIDTH.equals(name)) {
				this.strokeWidth = XmlUtils.parseNonNegativeFloat(name, value);
			} else if (STROKE_DASHARRAY.equals(name)) {
				this.stroke.setDashPathEffect(parseFloatArray(name, value));
			} else if (STROKE_LINECAP.equals(name)) {
				this.stroke.setStrokeCap(Cap.valueOf(value.toUpperCase(Locale.ENGLISH)));
			} else {
				throw XmlUtils.createSAXException(elementName, name, value, i);
			}
		}
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
