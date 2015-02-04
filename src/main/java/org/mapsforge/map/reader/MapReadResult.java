/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

import java.util.List;

/**
 * An immutable container for the data returned by the {@link MapDatabase}.
 */
public class MapReadResult {
	/**
	 * True if the read area is completely covered by water, false otherwise.
	 */
	public final boolean isWater;

	/**
	 * The read POIs.
	 */
	public final List<PointOfInterest> pointOfInterests;

	/**
	 * The read ways.
	 */
	public final List<Way> ways;

	MapReadResult(final MapReadResultBuilder mapReadResultBuilder) {
		this.pointOfInterests = mapReadResultBuilder.getPointOfInterests();
		this.ways = mapReadResultBuilder.getWays();
		this.isWater = mapReadResultBuilder.isWater();
	}
}
