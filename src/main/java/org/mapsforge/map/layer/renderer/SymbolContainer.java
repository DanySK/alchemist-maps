/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.Point;

public class SymbolContainer {
	private final boolean alignCenter;
	private final Point point;
	private final Bitmap symbol;
	private final float theta;

	public SymbolContainer(final Bitmap symbol, final Point point) {
		this(symbol, point, false, 0);
	}

	public SymbolContainer(final Bitmap symbol, final Point point, final boolean alignCenter, final float theta) {
		this.symbol = symbol;
		this.point = point;
		this.alignCenter = alignCenter;
		this.theta = theta;
	}

	public Point getPoint() {
		return point;
	}

	public Bitmap getSymbol() {
		return symbol;
	}

	public float getTheta() {
		return theta;
	}

	public boolean isAlignCenter() {
		return alignCenter;
	}
}
