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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.IOUtils;
import org.mapsforge.core.util.LRUCache;
import org.mapsforge.map.layer.queue.Job;

/**
 * A thread-safe cache for image files with a fixed size and LRU policy.
 */
public class FileSystemTileCache implements TileCache {
	private static final Logger LOGGER = Logger.getLogger(FileSystemTileCache.class.getName());
	public static final String FILE_EXTENSION = ".tile";

	private final ByteBuffer byteBuffer;

	private final File cacheDirectory;
	private long cacheId;
	private final GraphicFactory graphicFactory;
	private final LRUCache<Job, File> lruCache;

	private static File checkDirectory(final File file) {
		if (!file.exists() && !file.mkdirs()) {
			throw new IllegalArgumentException("could not create directory: " + file);
		} else if (!file.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + file);
		} else if (!file.canRead()) {
			throw new IllegalArgumentException("cannot read directory: " + file);
		} else if (!file.canWrite()) {
			throw new IllegalArgumentException("cannot write directory: " + file);
		}
		return file;
	}

	/**
	 * @param capacity
	 *            the maximum number of entries in this cache.
	 * @param cacheDirectory
	 *            the directory where cached tiles will be stored.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public FileSystemTileCache(final int capacity, final File cacheDirectory, final GraphicFactory graphicFactory) {
		this.lruCache = new FileLRUCache<Job>(capacity);
		this.cacheDirectory = checkDirectory(cacheDirectory);
		this.graphicFactory = graphicFactory;

		this.byteBuffer = ByteBuffer.allocate(Tile.TILE_SIZE * Tile.TILE_SIZE * 4);
	}

	@Override
	public synchronized boolean containsKey(final Job key) {
		return this.lruCache.containsKey(key);
	}

	@Override
	public synchronized void destroy() {
		this.lruCache.clear();

		final File[] filesToDelete = this.cacheDirectory.listFiles(ImageFileNameFilter.getInstance());
		if (filesToDelete != null) {
			for (final File file : filesToDelete) {
				if (file.exists() && !file.delete()) {
					LOGGER.log(Level.SEVERE, "could not delete file: " + file);
				}
			}
		}
	}

	@Override
	public synchronized Bitmap get(final Job key) {
		final File file = this.lruCache.get(key);
		if (file == null) {
			return null;
		}

		InputStream inputStream = null;
		try {
			final byte[] bytesArray = this.byteBuffer.array();
			inputStream = new FileInputStream(file);
			final int bytesRead = inputStream.read(bytesArray);
			if (bytesRead != file.length()) {
				this.lruCache.remove(key);
				LOGGER.log(Level.SEVERE, "could not read file: " + file);
				return null;
			}

			this.byteBuffer.rewind();
			final Bitmap bitmap = this.graphicFactory.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE);
			bitmap.copyPixelsFromBuffer(this.byteBuffer);
			return bitmap;
		} catch (final IOException e) {
			this.lruCache.remove(key);
			LOGGER.log(Level.SEVERE, null, e);
			return null;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Override
	public synchronized int getCapacity() {
		return this.lruCache.getCapacity();
	}

	private File getOutputFile() {
		while (true) {
			++this.cacheId;
			final File file = new File(this.cacheDirectory, this.cacheId + FILE_EXTENSION);
			if (!file.exists()) {
				return file;
			}
		}
	}

	@Override
	public synchronized void put(final Job key, final Bitmap bitmap) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		} else if (bitmap == null) {
			throw new IllegalArgumentException("bitmap must not be null");
		}

		if (this.lruCache.getCapacity() == 0) {
			return;
		}

		OutputStream outputStream = null;
		try {
			final File file = getOutputFile();

			this.byteBuffer.rewind();
			bitmap.copyPixelsToBuffer(this.byteBuffer);

			outputStream = new FileOutputStream(file);
			outputStream.write(this.byteBuffer.array(), 0, this.byteBuffer.position());

			this.lruCache.put(key, file);
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}
}
