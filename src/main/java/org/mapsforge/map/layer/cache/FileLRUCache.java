/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.cache;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapsforge.core.util.LRUCache;

class FileLRUCache<T> extends LRUCache<T, File> {
	private static final Logger LOGGER = Logger.getLogger(FileLRUCache.class.getName());
	private static final long serialVersionUID = 1L;

	FileLRUCache(final int capacity) {
		super(capacity);
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry<T, File> eldest) {
		if (size() > getCapacity()) {
			remove(eldest.getKey());
			final File file = eldest.getValue();
			if (file.exists() && !file.delete()) {
				LOGGER.log(Level.SEVERE, "could not delete file: " + file);
			}
		}

		return false;
	}
}
