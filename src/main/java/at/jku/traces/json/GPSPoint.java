/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package at.jku.traces.json;

import it.unibo.alchemist.model.implementations.positions.LatLongPosition;
import it.unibo.alchemist.model.interfaces.IGPSPoint;
import it.unibo.alchemist.model.interfaces.IPosition;

/**
 * @author Danilo Pianini
 * 
 */
public class GPSPoint implements IGPSPoint {

	private static final long serialVersionUID = -6060550940453129358L;
	private final double lo;
	private double t;
	private final double la;

	/**
	 * @param latitude
	 *            latitude
	 * @param longitude
	 *            longitude
	 * @param time
	 *            time
	 */
	public GPSPoint(final double latitude, final double longitude, final double time) {
		lo = longitude;
		la = latitude;
		t = time;
	}

	/**
	 * @param latlong
	 *            latitude and longitude
	 * @param time
	 *            time
	 */
	public GPSPoint(final LatLongPosition latlong, final double time) {
		this(latlong.getLatitude(), latlong.getLongitude(), time);
	}

	@Override
	public int compareTo(final IGPSPoint p) {
		return (int) Math.signum(t - p.getTime());
	}

	@Override
	public double getLatitude() {
		return la;
	}

	@Override
	public double getLongitude() {
		return lo;
	}

	@Override
	public double getTime() {
		return t;
	}

	@Override
	public void setTime(final double d) {
		t = d;
	}

	@Override
	public IPosition toIPosition() {
		return new LatLongPosition(la, lo);
	}

	@Override
	public String toString() {
		return "[" + la + "," + lo + "]@" + t;
	}

}
