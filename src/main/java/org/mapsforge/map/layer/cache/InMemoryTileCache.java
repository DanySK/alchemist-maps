/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.cache;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.util.LRUCache;
import org.mapsforge.map.layer.queue.Job;

/**
 * A thread-safe cache for object images with a variable size and LRU policy.
 */
public class InMemoryTileCache implements TileCache {
	private LRUCache<Job, Bitmap> lruCache;

	/**
	 * @param capacity
	 *            the maximum number of entries in this cache.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public InMemoryTileCache(final int capacity) {
		this.lruCache = new LRUCache<Job, Bitmap>(capacity);
	}

	@Override
	public synchronized boolean containsKey(final Job key) {
		return this.lruCache.containsKey(key);
	}

	@Override
	public synchronized void destroy() {
		this.lruCache.clear();
	}

	@Override
	public synchronized Bitmap get(final Job key) {
		return this.lruCache.get(key);
	}

	@Override
	public synchronized int getCapacity() {
		return this.lruCache.getCapacity();
	}

	@Override
	public synchronized void put(final Job key, final Bitmap bitmap) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		} else if (bitmap == null) {
			throw new IllegalArgumentException("bitmap must not be null");
		}

		this.lruCache.put(key, bitmap);
	}

	/**
	 * Sets the new size of this cache. If this cache already contains more
	 * items than the new capacity allows, items are discarded based on the
	 * cache policy.
	 * 
	 * @param capacity
	 *            the new maximum number of entries in this cache.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public synchronized void setCapacity(final int capacity) {
		final LRUCache<Job, Bitmap> lruCacheNew = new LRUCache<Job, Bitmap>(capacity);
		lruCacheNew.putAll(this.lruCache);
		this.lruCache = lruCacheNew;
	}
}
