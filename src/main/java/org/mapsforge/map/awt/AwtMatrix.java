/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.awt;

import java.awt.geom.AffineTransform;

import org.mapsforge.core.graphics.Matrix;

public class AwtMatrix implements Matrix {
	private final AffineTransform affineTransform = new AffineTransform();

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	@Override
	public void reset() {
		this.affineTransform.setToIdentity();
	}

	@Override
	public void rotate(final float theta) {
		this.affineTransform.rotate(theta);
	}

	@Override
	public void rotate(final float theta, final float pivotX, final float pivotY) {
		this.affineTransform.rotate(theta, pivotX, pivotY);
	}

	@Override
	public void scale(final float scaleX, final float scaleY) {
		this.affineTransform.scale(scaleX, scaleY);
	}

	@Override
	public void translate(final float translateX, final float translateY) {
		this.affineTransform.translate(translateX, translateY);
	}
}
