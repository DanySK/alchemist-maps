/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.util.List;

import org.mapsforge.core.model.Tag;

public class MatchingCacheKey {
	private static final int PRIME = 31;
	private final Closed closed;
	private final List<Tag> tags;
	private final byte zoomLevel;

	public MatchingCacheKey(final List<Tag> tags, final byte zoomLevel, final Closed closed) {
		this.tags = tags;
		this.zoomLevel = zoomLevel;
		this.closed = closed;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof MatchingCacheKey)) {
			return false;
		}
		final MatchingCacheKey other = (MatchingCacheKey) obj;
		if (this.closed != other.closed) {
			return false;
		}
		if (this.tags == null) {
			if (other.tags != null) {
				return false;
			}
		} else if (!this.tags.equals(other.tags)) {
			return false;
		}
		if (this.zoomLevel != other.zoomLevel) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = PRIME * result + (this.closed == null ? 0 : this.closed.hashCode());
		result = PRIME * result + (this.tags == null ? 0 : this.tags.hashCode());
		result = PRIME * result + this.zoomLevel;
		return result;
	}
}
