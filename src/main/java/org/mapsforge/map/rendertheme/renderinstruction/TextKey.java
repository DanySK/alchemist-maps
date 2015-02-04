/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import java.util.List;

import org.mapsforge.core.model.Tag;

final class TextKey {
	private static final String KEY_ELEVATION = "ele";
	private static final String KEY_HOUSENUMBER = "addr:housenumber";
	private static final String KEY_NAME = "name";
	private static final String KEY_REF = "ref";
	private static final TextKey TEXT_KEY_ELEVATION = new TextKey(KEY_ELEVATION);
	private static final TextKey TEXT_KEY_HOUSENUMBER = new TextKey(KEY_HOUSENUMBER);
	private static final TextKey TEXT_KEY_NAME = new TextKey(KEY_NAME);
	private static final TextKey TEXT_KEY_REF = new TextKey(KEY_REF);

	private final String key;

	public static TextKey getInstance(final String key) {
		if (KEY_ELEVATION.equals(key)) {
			return TEXT_KEY_ELEVATION;
		} else if (KEY_HOUSENUMBER.equals(key)) {
			return TEXT_KEY_HOUSENUMBER;
		} else if (KEY_NAME.equals(key)) {
			return TEXT_KEY_NAME;
		} else if (KEY_REF.equals(key)) {
			return TEXT_KEY_REF;
		} else {
			throw new IllegalArgumentException("invalid key: " + key);
		}
	}

	private TextKey(final String key) {
		this.key = key;
	}

	public String getValue(final List<Tag> tags) {
		for (int i = 0, n = tags.size(); i < n; ++i) {
			if (this.key.equals(tags.get(i).getKey())) {
				return tags.get(i).getValue();
			}
		}
		return null;
	}
}
