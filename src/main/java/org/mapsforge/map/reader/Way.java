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
 * An immutable container for all data associated with a single way or area
 * (closed way).
 */
public class Way {
	/**
	 * The position of the area label (may be null).
	 */
	private final LatLong labelPosition;

	/**
	 * The geographical coordinates of the way nodes.
	 */
	private final LatLong[][] latLongs;

	/**
	 * The layer of this way + 5 (to avoid negative values).
	 */
	private final byte layer;

	/**
	 * The tags of this way.
	 */
	private final List<Tag> tags;

	public Way(final byte layer, final List<Tag> tags, final LatLong[][] latLongs, final LatLong labelPosition) {
		this.layer = layer;
		this.tags = tags;
		this.latLongs = latLongs;
		this.labelPosition = labelPosition;
	}

	public LatLong getLabelPosition() {
		return labelPosition;
	}

	public LatLong[][] getLatLongs() {
		return latLongs;
	}

	public byte getLayer() {
		return layer;
	}

	public List<Tag> getTags() {
		return tags;
	}
}
