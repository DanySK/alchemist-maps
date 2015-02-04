/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

/**
 * An utility class to convert byte arrays to numbers.
 */
public final class Deserializer {
	/**
	 * Converts five bytes of a byte array to an unsigned long.
	 * <p>
	 * The byte order is big-endian.
	 * 
	 * @param buffer
	 *            the byte array.
	 * @param offset
	 *            the offset in the array.
	 * @return the long value.
	 */
	public static long getFiveBytesLong(final byte[] buffer, final int offset) {
		return (buffer[offset] & 0xffL) << 32 | (buffer[offset + 1] & 0xffL) << 24 | (buffer[offset + 2] & 0xffL) << 16 | (buffer[offset + 3] & 0xffL) << 8 | buffer[offset + 4] & 0xffL;
	}

	/**
	 * Converts four bytes of a byte array to a signed int.
	 * <p>
	 * The byte order is big-endian.
	 * 
	 * @param buffer
	 *            the byte array.
	 * @param offset
	 *            the offset in the array.
	 * @return the int value.
	 */
	public static int getInt(final byte[] buffer, final int offset) {
		return buffer[offset] << 24 | (buffer[offset + 1] & 0xff) << 16 | (buffer[offset + 2] & 0xff) << 8 | buffer[offset + 3] & 0xff;
	}

	/**
	 * Converts eight bytes of a byte array to a signed long.
	 * <p>
	 * The byte order is big-endian.
	 * 
	 * @param buffer
	 *            the byte array.
	 * @param offset
	 *            the offset in the array.
	 * @return the long value.
	 */
	public static long getLong(final byte[] buffer, final int offset) {
		return (buffer[offset] & 0xffL) << 56 | (buffer[offset + 1] & 0xffL) << 48 | (buffer[offset + 2] & 0xffL) << 40 | (buffer[offset + 3] & 0xffL) << 32 | (buffer[offset + 4] & 0xffL) << 24 | (buffer[offset + 5] & 0xffL) << 16 | (buffer[offset + 6] & 0xffL) << 8 | buffer[offset + 7] & 0xffL;
	}

	/**
	 * Converts two bytes of a byte array to a signed int.
	 * <p>
	 * The byte order is big-endian.
	 * 
	 * @param buffer
	 *            the byte array.
	 * @param offset
	 *            the offset in the array.
	 * @return the int value.
	 */
	public static int getShort(final byte[] buffer, final int offset) {
		return buffer[offset] << 8 | buffer[offset + 1] & 0xff;
	}

	private Deserializer() {
		throw new IllegalStateException();
	}
}
