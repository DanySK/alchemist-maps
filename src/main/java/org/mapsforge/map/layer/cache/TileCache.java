/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.cache;

import java.util.Map;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.map.layer.queue.Job;

/**
 * Interface for tile image caches.
 */
public interface TileCache {
	/**
	 * @return true if this cache contains an image for the given key, false
	 *         otherwise.
	 * @see Map#containsKey
	 */
	boolean containsKey(Job key);

	/**
	 * Destroys this cache.
	 */
	void destroy();

	/**
	 * @return the image for the given key or null, if this cache contains no
	 *         image for the key.
	 * @see Map#get
	 */
	Bitmap get(Job key);

	/**
	 * @return the capacity of this cache.
	 */
	int getCapacity();

	/**
	 * @throws IllegalArgumentException
	 *             if any of the parameters is {@code null}.
	 * @see Map#put
	 */
	void put(Job key, Bitmap bitmap);
}
