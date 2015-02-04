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
import java.io.IOException;
import java.io.InputStream;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.xml.sax.SAXException;

public final class XmlUtils {
	private static final String PREFIX_FILE = "file:";
	private static final String PREFIX_JAR = "jar:";
	private static final int INITIAL_CAPACITY = 100;

	private static void checkForNegativeValue(final String name, final float value) throws SAXException {
		if (value < 0) {
			throw new SAXException("Attribute '" + name + "' must not be negative: " + value);
		}
	}

	public static void checkMandatoryAttribute(final String elementName, final String attributeName, final Object attributeValue) throws SAXException {
		if (attributeValue == null) {
			throw new SAXException("missing attribute '" + attributeName + "' for element: " + elementName);
		}
	}

	public static Bitmap createBitmap(final GraphicFactory graphicFactory, final String relativePathPrefix, final String src) throws IOException {
		if (src == null || src.length() == 0) {
			// no image source defined
			return null;
		}

		final InputStream inputStream = createInputStream(relativePathPrefix, src);
		final Bitmap bitmap = graphicFactory.createBitmap(inputStream);
		inputStream.close();
		return bitmap;
	}

	private static InputStream createInputStream(final String relativePathPrefix, final String src) throws FileNotFoundException {
		if (src.startsWith(PREFIX_JAR)) {
			final String absoluteName = getAbsoluteName(relativePathPrefix, src.substring(PREFIX_JAR.length()));
			final InputStream inputStream = XmlUtils.class.getResourceAsStream(absoluteName);
			if (inputStream == null) {
				throw new FileNotFoundException("resource not found: " + absoluteName);
			}
			return inputStream;
		} else if (src.startsWith(PREFIX_FILE)) {
			final File file = getFile(relativePathPrefix, src.substring(PREFIX_FILE.length()));
			if (!file.exists()) {
				throw new FileNotFoundException("file does not exist: " + file.getAbsolutePath());
			} else if (!file.isFile()) {
				throw new FileNotFoundException("not a file: " + file.getAbsolutePath());
			} else if (!file.canRead()) {
				throw new FileNotFoundException("cannot read file: " + file.getAbsolutePath());
			}
			return new FileInputStream(file);
		}

		throw new FileNotFoundException("invalid bitmap source: " + src);
	}

	public static SAXException createSAXException(final String element, final String name, final String value, final int attributeIndex) {
		final StringBuilder stringBuilder = new StringBuilder(INITIAL_CAPACITY);
		stringBuilder.append("unknown attribute (");
		stringBuilder.append(attributeIndex);
		stringBuilder.append(") in element '");
		stringBuilder.append(element);
		stringBuilder.append("': ");
		stringBuilder.append(name);
		stringBuilder.append('=');
		stringBuilder.append(value);

		return new SAXException(stringBuilder.toString());
	}

	private static String getAbsoluteName(final String relativePathPrefix, final String name) {
		if (name.charAt(0) == '/') {
			return name;
		}
		return relativePathPrefix + name;
	}

	private static File getFile(final String parentPath, final String pathName) {
		if (pathName.charAt(0) == File.separatorChar) {
			return new File(pathName);
		}
		return new File(parentPath, pathName);
	}

	public static byte parseNonNegativeByte(final String name, final String value) throws SAXException {
		final byte parsedByte = Byte.parseByte(value);
		checkForNegativeValue(name, parsedByte);
		return parsedByte;
	}

	public static float parseNonNegativeFloat(final String name, final String value) throws SAXException {
		final float parsedFloat = Float.parseFloat(value);
		checkForNegativeValue(name, parsedFloat);
		return parsedFloat;
	}

	public static int parseNonNegativeInteger(final String name, final String value) throws SAXException {
		final int parsedInt = Integer.parseInt(value);
		checkForNegativeValue(name, parsedInt);
		return parsedInt;
	}

	private XmlUtils() {
		throw new IllegalStateException();
	}
}
