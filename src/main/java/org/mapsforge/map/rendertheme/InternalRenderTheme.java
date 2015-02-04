/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme;

import java.io.InputStream;

/**
 * Enumeration of all internal rendering themes.
 */
public enum InternalRenderTheme implements XmlRenderTheme {
	/**
	 * A render-theme similar to the OpenStreetMap Osmarender style.
	 * 
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/Osmarender">Osmarender</a>
	 */
	OSMARENDER("/osmarender/", "osmarender.xml");

	private final String absolutePath;
	private final String file;

	private InternalRenderTheme(final String absolutePath, final String file) {
		this.absolutePath = absolutePath;
		this.file = file;
	}

	@Override
	public String getRelativePathPrefix() {
		return this.absolutePath;
	}

	@Override
	public InputStream getRenderThemeAsStream() {
		return Thread.currentThread().getClass().getResourceAsStream(this.absolutePath + this.file);
	}
}
