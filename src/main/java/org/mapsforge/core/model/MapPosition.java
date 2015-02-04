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
 * A MapPosition represents an immutable pair of {@link LatLong} and zoom level.
 * 
 * @author Mapsforge
 * @author Giovanni Ciatto
 * @author Danilo Pianini
 *
 */
public class MapPosition implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int EXTIMED_SIZE = 50;

	private final LatLong latLong;
	private final byte zoomLevel;
	
	private int hash;

	/**
	 * @param pos
	 *            the geographical coordinates of the map center.
	 * @param zoom
	 *            the zoom level of the map.
	 */
	public MapPosition(final LatLong pos, final byte zoom) {
		if (pos == null) {
			throw new IllegalArgumentException("latLong must not be null");
		} else if (zoom < 0) {
			throw new IllegalArgumentException("zoomLevel must not be negative: " + zoom);
		}
		this.latLong = pos;
		this.zoomLevel = zoom;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof MapPosition)) {
			return false;
		}
		final MapPosition other = (MapPosition) obj;
		return latLong == null && other.latLong == null || latLong != null && latLong.equals(other.latLong) && zoomLevel == other.zoomLevel;
	}

	/**
	 * @return latlong
	 */
	public LatLong getLatLong() {
		return latLong;
	}

	/**
	 * @return zoom level
	 */
	public byte getZoomLevel() {
		return zoomLevel;
	}

	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = HashUtils.djb2int32obj(latLong, zoomLevel);
		}
		return hash;
	}

	@Override
	public String toString() {
		// final int EXTIMED_SIZE = 50;
		final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
		stringBuilder.append("latLong=");
		stringBuilder.append(this.latLong);
		stringBuilder.append(", zoomLevel=");
		stringBuilder.append(this.zoomLevel);
		return stringBuilder.toString();
	}
}
