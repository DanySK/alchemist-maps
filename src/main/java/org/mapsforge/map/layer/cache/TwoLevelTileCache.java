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
import org.mapsforge.map.layer.queue.Job;

public class TwoLevelTileCache implements TileCache {
    private final TileCache firstLevelTileCache;
    private final TileCache secondLevelTileCache;

    public TwoLevelTileCache(final TileCache firstLevelTileCache, final TileCache secondLevelTileCache) {
        this.firstLevelTileCache = firstLevelTileCache;
        this.secondLevelTileCache = secondLevelTileCache;
    }

    @Override
    public synchronized boolean containsKey(final Job key) {
        return this.firstLevelTileCache.containsKey(key) || this.secondLevelTileCache.containsKey(key);
    }

    @Override
    public synchronized void destroy() {
        this.firstLevelTileCache.destroy();
        this.secondLevelTileCache.destroy();
    }

    @Override
    public synchronized Bitmap get(final Job key) {
        Bitmap returnBitmap = this.firstLevelTileCache.get(key);
        if (returnBitmap != null) {
            return returnBitmap;
        }

        returnBitmap = this.secondLevelTileCache.get(key);
        if (returnBitmap != null) {
            this.firstLevelTileCache.put(key, returnBitmap);
            return returnBitmap;
        }

        return null;
    }

    @Override
    public synchronized int getCapacity() {
        return Math.max(this.firstLevelTileCache.getCapacity(), this.secondLevelTileCache.getCapacity());
    }

    @Override
    public synchronized void put(final Job key, final Bitmap bitmap) {
        this.secondLevelTileCache.put(key, bitmap);
    }
}
