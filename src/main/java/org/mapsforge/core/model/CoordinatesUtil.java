/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A utility class to convert, parse and validate geographical coordinates.
 * 
 * @author Mapsforge
 * 
 */
public final class CoordinatesUtil {
	/**
	 * Maximum possible latitude coordinate.
	 */
	public static final double LATITUDE_MAX = 90;

	/**
	 * Minimum possible latitude coordinate.
	 */
	public static final double LATITUDE_MIN = -LATITUDE_MAX;

	/**
	 * Maximum possible longitude coordinate.
	 */
	public static final double LONGITUDE_MAX = 180;

	/**
	 * Minimum possible longitude coordinate.
	 */
	public static final double LONGITUDE_MIN = -LONGITUDE_MAX;

	/**
	 * Conversion factor from degrees to microdegrees.
	 */
	private static final double CONVERSION_FACTOR = 1000000.0;

	private static final String DELIMITER = ",";

	/**
	 * Converts a coordinate from degrees to microdegrees (degrees * 10^6). No
	 * validation is performed.
	 * 
	 * @param coordinate
	 *            the coordinate in degrees.
	 * @return the coordinate in microdegrees (degrees * 10^6).
	 */
	public static int degreesToMicrodegrees(final double coordinate) {
		return (int) (coordinate * CONVERSION_FACTOR);
	}

	/**
	 * Converts a coordinate from microdegrees (degrees * 10^6) to degrees. No
	 * validation is performed.
	 * 
	 * @param coordinate
	 *            the coordinate in microdegrees (degrees * 10^6).
	 * @return the coordinate in degrees.
	 */
	public static double microdegreesToDegrees(final int coordinate) {
		return coordinate / CONVERSION_FACTOR;
	}

	/**
	 * Parses a given number of comma-separated coordinate values from the
	 * supplied string.
	 * 
	 * @param coordinatesString
	 *            a comma-separated string of coordinate values.
	 * @param numberOfCoordinates
	 *            the expected number of coordinate values in the string.
	 * @return the coordinate values in the order they have been parsed from the
	 *         string.
	 */
	public static double[] parseCoordinateString(final String coordinatesString, final int numberOfCoordinates) {
		final StringTokenizer stringTokenizer = new StringTokenizer(coordinatesString, DELIMITER, true);
		final List<String> tokens = new ArrayList<String>(numberOfCoordinates);
		boolean isDelimiter = true;
		while (stringTokenizer.hasMoreTokens()) {
			final String token = stringTokenizer.nextToken();
			isDelimiter ^= true;
			if (isDelimiter) {
				continue;
			}
			tokens.add(token);
		}

		if (isDelimiter) {
			throw new IllegalArgumentException("invalid coordinate delimiter: " + coordinatesString);
		} else if (tokens.size() != numberOfCoordinates) {
			throw new IllegalArgumentException("invalid number of coordinate values: " + coordinatesString);
		}

		final double[] coordinates = new double[numberOfCoordinates];
		for (int i = 0; i < numberOfCoordinates; ++i) {
			coordinates[i] = Double.parseDouble(tokens.get(i));
		}
		return coordinates;
	}

	/**
	 * @param latitude
	 *            the latitude coordinate in degrees which should be validated.
	 */
	public static void validateLatitude(final double latitude) {
		if (Double.isNaN(latitude) || latitude < LATITUDE_MIN || latitude > LATITUDE_MAX) {
			throw new IllegalArgumentException("invalid latitude: " + latitude);
		}
	}

	/**
	 * @param longitude
	 *            the longitude coordinate in degrees which should be validated.
	 */
	public static void validateLongitude(final double longitude) {
		if (Double.isNaN(longitude) || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
			throw new IllegalArgumentException("invalid longitude: " + longitude);
		}
	}

	private CoordinatesUtil() {
		throw new IllegalStateException();
	}
}
