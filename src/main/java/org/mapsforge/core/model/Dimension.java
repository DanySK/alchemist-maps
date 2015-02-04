/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.model;

import java.io.Serializable;

import org.danilopianini.lang.HashUtils;

/**
 * @author Mapsforge
 * 
 */
public class Dimension implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int EXTIMED_SIZE = 50;

	private final int height;
	private final int width;

	/**
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public Dimension(final int w, final int h) {
		if (w < 0) {
			throw new IllegalArgumentException("width must not be negative: " + w);
		} else if (h < 0) {
			throw new IllegalArgumentException("height must not be negative: " + h);
		}

		this.width = w;
		this.height = h;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Dimension)) {
			return false;
		}
		final Dimension other = (Dimension) obj;
		return width == other.width && height == other.height;
	}

	/**
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		return HashUtils.djb2int32(width, height);
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
		stringBuilder.append("width=");
		stringBuilder.append(this.width);
		stringBuilder.append(", height=");
		stringBuilder.append(this.height);
		return stringBuilder.toString();
	}
}
