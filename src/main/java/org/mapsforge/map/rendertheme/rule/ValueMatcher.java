/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.Tag;

public class ValueMatcher implements AttributeMatcher {
	private final List<String> values;

	public ValueMatcher(final List<String> values) {
		this.values = values;
	}

	@Override
	public boolean isCoveredBy(final AttributeMatcher attributeMatcher) {
		if (attributeMatcher == this) {
			return true;
		}

		final List<Tag> tags = new ArrayList<Tag>(this.values.size());
		final int n = this.values.size();
		for (int i = 0; i < n; ++i) {
			tags.add(new Tag(null, this.values.get(i)));
		}
		return attributeMatcher.matches(tags);
	}

	@Override
	public boolean matches(final List<Tag> tags) {
		final int n = tags.size();
		for (int i = 0; i < n; ++i) {
			if (this.values.contains(tags.get(i).getValue())) {
				return true;
			}
		}
		return false;
	}
}
