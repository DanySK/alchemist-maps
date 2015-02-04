/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.model.Point;

public class CircleContainer implements ShapeContainer {
	private final Point point;
	private final float radius;

	public CircleContainer(final Point point, final float radius) {
		this.point = point;
		this.radius = radius;
	}

	public Point getPoint() {
		return point;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}
}
