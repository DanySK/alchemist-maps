/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.core.model.Point;

public final class GeometryUtils {
	/**
	 * Calculates the center of the minimum bounding rectangle for the given
	 * coordinates.
	 * 
	 * @param coordinates
	 *            the coordinates for which calculation should be done.
	 * @return the center coordinates of the minimum bounding rectangle.
	 */
	public static Point calculateCenterOfBoundingBox(final Point[] coordinates) {
		double pointXMin = coordinates[0].getX();
		double pointXMax = coordinates[0].getX();
		double pointYMin = coordinates[0].getY();
		double pointYMax = coordinates[0].getY();

		for (int i = 1; i < coordinates.length; ++i) {
			final Point immutablePoint = coordinates[i];
			if (immutablePoint.getX() < pointXMin) {
				pointXMin = immutablePoint.getX();
			} else if (immutablePoint.getX() > pointXMax) {
				pointXMax = immutablePoint.getX();
			}

			if (immutablePoint.getY() < pointYMin) {
				pointYMin = immutablePoint.getY();
			} else if (immutablePoint.getY() > pointYMax) {
				pointYMax = immutablePoint.getY();
			}
		}

		return new Point((pointXMin + pointXMax) / 2, (pointYMax + pointYMin) / 2);
	}

	/**
	 * @param way
	 *            the coordinates of the way.
	 * @return true if the given way is closed, false otherwise.
	 */
	public static boolean isClosedWay(final Point[] way) {
		return way[0].equals(way[way.length - 1]);
	}

	private GeometryUtils() {
		throw new IllegalStateException();
	}
}
