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
import java.io.FilenameFilter;

final class ImageFileNameFilter implements FilenameFilter {
	private static final ImageFileNameFilter INSTANCE = new ImageFileNameFilter();

	public static ImageFileNameFilter getInstance() {
		return INSTANCE;
	}

	private ImageFileNameFilter() {
		// do nothing
	}

	@Override
	public boolean accept(final File directory, final String fileName) {
		return fileName.endsWith(FileSystemTileCache.FILE_EXTENSION);
	}
}
