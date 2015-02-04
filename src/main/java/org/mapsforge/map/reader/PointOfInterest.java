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

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Tag;

/**
 * An immutable container for all data associated with a single point of
 * interest node (POI).
 */
public class PointOfInterest {
	/**
	 * The layer of this POI + 5 (to avoid negative values).
	 */
	private final byte layer;

	/**
	 * The position of this POI.
	 */
	private final LatLong position;

	/**
	 * The tags of this POI.
	 */
	private final List<Tag> tags;

	public PointOfInterest(final byte layer, final List<Tag> tags, final LatLong position) {
		this.layer = layer;
		this.tags = tags;
		this.position = position;
	}

	public byte getLayer() {
		return layer;
	}

	public LatLong getPosition() {
		return position;
	}

	public List<Tag> getTags() {
		return tags;
	}

}
