/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An ExternalRenderTheme allows for customizing the rendering style of the map
 * via an XML file.
 */
public class ExternalRenderTheme implements XmlRenderTheme {
	private static final long serialVersionUID = 1L;

	private final long lastModifiedTime;
	private final File renderThemeFile;

	/**
	 * @param renderThemeFile
	 *            the XML render theme file.
	 * @throws FileNotFoundException
	 *             if the file does not exist or cannot be read.
	 */
	public ExternalRenderTheme(final File renderThemeFile) throws FileNotFoundException {
		if (!renderThemeFile.exists()) {
			throw new FileNotFoundException("file does not exist: " + renderThemeFile.getAbsolutePath());
		} else if (!renderThemeFile.isFile()) {
			throw new FileNotFoundException("not a file: " + renderThemeFile.getAbsolutePath());
		} else if (!renderThemeFile.canRead()) {
			throw new FileNotFoundException("cannot read file: " + renderThemeFile.getAbsolutePath());
		}

		this.lastModifiedTime = renderThemeFile.lastModified();
		if (this.lastModifiedTime == 0L) {
			throw new FileNotFoundException("cannot read last modified time: " + renderThemeFile.getAbsolutePath());
		}
		this.renderThemeFile = renderThemeFile;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ExternalRenderTheme)) {
			return false;
		}
		final ExternalRenderTheme other = (ExternalRenderTheme) obj;
		if (this.lastModifiedTime != other.lastModifiedTime) {
			return false;
		}
		if (this.renderThemeFile == null) {
			if (other.renderThemeFile != null) {
				return false;
			}
		} else if (!this.renderThemeFile.equals(other.renderThemeFile)) {
			return false;
		}
		return true;
	}

	@Override
	public String getRelativePathPrefix() {
		return this.renderThemeFile.getParent();
	}

	@Override
	public InputStream getRenderThemeAsStream() throws FileNotFoundException {
		return new FileInputStream(this.renderThemeFile);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.lastModifiedTime ^ this.lastModifiedTime >>> 32);
		result = prime * result + (this.renderThemeFile == null ? 0 : this.renderThemeFile.hashCode());
		return result;
	}
}
