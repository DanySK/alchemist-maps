/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRUCache with a fixed size and an access-order policy. Old mappings are
 * automatically removed from the cache when new mappings are added. This
 * implementation uses an {@link LinkedHashMap} internally.
 * 
 * @author Mapsforge
 * @author Giovanni Ciatto
 * @author Danilo Pianini
 *
 * @param <K>
 *            the type of the map key, see {@link Map}.
 * @param <V>
 *            the type of the map value, see {@link Map}.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	private static final float LOAD_FACTOR = 0.6f;
	private static final long serialVersionUID = 1L;
	private final int capacity;

	private static int calculateInitialCapacity(final int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("capacity must not be negative: " + capacity);
		}
		return (int) (capacity / LOAD_FACTOR) + 2;
	}

	/**
	 * @param cpy
	 *            the maximum capacity of this cache.
	 */
	public LRUCache(final int cpy) {
		super(calculateInitialCapacity(cpy), LOAD_FACTOR, true);
		this.capacity = cpy;
	}

	/**
	 * @return capacity
	 */
	public int getCapacity() {
		return this.capacity;
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return size() > this.capacity;
	}
}
