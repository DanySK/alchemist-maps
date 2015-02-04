/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model;

import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.model.common.Observable;

public class FrameBufferModel extends Observable {
	private Dimension dimension;
	private MapPosition mapPosition;
	private double overdrawFactor = 1.5;

	/**
	 * @return the current dimension of the {@code FrameBuffer} (may be null).
	 */
	public synchronized Dimension getDimension() {
		return this.dimension;
	}

	/**
	 * @return the current {@code MapPosition} of the {@code FrameBuffer} (may
	 *         be null).
	 */
	public synchronized MapPosition getMapPosition() {
		return this.mapPosition;
	}

	public synchronized double getOverdrawFactor() {
		return this.overdrawFactor;
	}

	public void setDimension(final Dimension dimension) {
		synchronized (this) {
			this.dimension = dimension;
		}
		notifyObservers();
	}

	public void setMapPosition(final MapPosition mapPosition) {
		synchronized (this) {
			this.mapPosition = mapPosition;
		}
		notifyObservers();
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the {@code overdrawFactor} is less or equal zero.
	 */
	public void setOverdrawFactor(final double overdrawFactor) {
		if (overdrawFactor <= 0) {
			throw new IllegalArgumentException("overdrawFactor must be > 0: " + overdrawFactor);
		}
		synchronized (this) {
			this.overdrawFactor = overdrawFactor;
		}
		notifyObservers();
	}
}
