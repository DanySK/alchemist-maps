/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Interface for a render theme which is defined in XML.
 */
public interface XmlRenderTheme extends Serializable {
	/**
	 * @return the prefix for all relative resource paths.
	 */
	String getRelativePathPrefix();

	/**
	 * @return an InputStream to read the render theme data from.
	 * @throws FileNotFoundException
	 *             if the render theme file cannot be found.
	 */
	InputStream getRenderThemeAsStream() throws FileNotFoundException;
}
