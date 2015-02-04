/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link Circle} instances.
 */
public class CircleBuilder {
	private static final String FILL = "fill";
	private static final String RADIUS = "radius";
	private static final String SCALE_RADIUS = "scale-radius";
	private static final String STROKE = "stroke";
	private static final String STROKE_WIDTH = "stroke-width";

	private final Paint fill;
	private final int level;
	private Float radius;
	private boolean scaleRadius;
	private final Paint stroke;
	private float strokeWidth;

	public CircleBuilder(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes, final int level) throws SAXException {
		this.level = level;

		this.fill = graphicFactory.createPaint();
		this.fill.setColor(graphicFactory.createColor(Color.TRANSPARENT));
		this.fill.setStyle(Style.FILL);

		this.stroke = graphicFactory.createPaint();
		this.stroke.setColor(graphicFactory.createColor(Color.TRANSPARENT));
		this.stroke.setStyle(Style.STROKE);

		extractValues(graphicFactory, elementName, attributes);
	}

	/**
	 * @return a new {@code Circle} instance.
	 */
	public Circle build() {
		return new Circle(this);
	}

	private void extractValues(final GraphicFactory graphicFactory, final String elementName, final Attributes attributes) throws SAXException {
		for (int i = 0; i < attributes.getLength(); ++i) {
			final String name = attributes.getQName(i);
			final String value = attributes.getValue(i);

			if (RADIUS.equals(name)) {
				this.radius = Float.valueOf(XmlUtils.parseNonNegativeFloat(name, value));
			} else if (SCALE_RADIUS.equals(name)) {
				this.scaleRadius = Boolean.parseBoolean(value);
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

		XmlUtils.checkMandatoryAttribute(elementName, RADIUS, this.radius);
	}

	public Paint getFill() {
		return fill;
	}

	public int getLevel() {
		return level;
	}

	public Float getRadius() {
		return radius;
	}

	public Paint getStroke() {
		return stroke;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public boolean isScaleRadius() {
		return scaleRadius;
	}

	public void setRadius(final Float radius) {
		this.radius = radius;
	}

	public void setScaleRadius(final boolean scaleRadius) {
		this.scaleRadius = scaleRadius;
	}

	public void setStrokeWidth(final float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
}
