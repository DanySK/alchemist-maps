/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.overlay;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.Layer;

/**
 * A {@code Polyline} draws a connected series of line segments specified by a
 * list of {@link LatLong LatLongs}.
 * <p>
 * A {@code Polyline} holds a {@link Paint} object which defines drawing
 * parameters such as color, stroke width, pattern and transparency.
 */
public class Polyline extends Layer {
	private final List<LatLong> latLongs = new CopyOnWriteArrayList<LatLong>();
	private Paint paintStroke;

	/**
	 * @param paintStroke
	 *            the initial {@code Paint} used to stroke this polyline (may be
	 *            null).
	 */
	public Polyline(final Paint paintStroke) {
		super();

		this.paintStroke = paintStroke;
	}

	@Override
	public synchronized void draw(final BoundingBox boundingBox, final byte zoomLevel, final Canvas canvas, final Point canvasPosition) {
		if (this.latLongs.isEmpty() || this.paintStroke == null) {
			return;
		}

		final Iterator<LatLong> iterator = this.latLongs.iterator();
		LatLong latLong = iterator.next();
		int previousX = (int) (MercatorProjection.longitudeToPixelX(latLong.getLongitude(), zoomLevel) - canvasPosition.getX());
		int previousY = (int) (MercatorProjection.latitudeToPixelY(latLong.getLatitude(), zoomLevel) - canvasPosition.getY());

		while (iterator.hasNext()) {
			latLong = iterator.next();
			final int x = (int) (MercatorProjection.longitudeToPixelX(latLong.getLongitude(), zoomLevel) - canvasPosition.getX());
			final int y = (int) (MercatorProjection.latitudeToPixelY(latLong.getLatitude(), zoomLevel) - canvasPosition.getY());

			canvas.drawLine(previousX, previousY, x, y, this.paintStroke);

			previousX = x;
			previousY = y;
		}
	}

	/**
	 * @return a thread-safe list of LatLongs in this polyline.
	 */
	public List<LatLong> getLatLongs() {
		return this.latLongs;
	}

	/**
	 * @return the {@code Paint} used to stroke this polyline (may be null).
	 */
	public synchronized Paint getPaintStroke() {
		return this.paintStroke;
	}

	/**
	 * @param paintStroke
	 *            the new {@code Paint} used to stroke this polyline (may be
	 *            null).
	 */
	public synchronized void setPaintStroke(final Paint paintStroke) {
		this.paintStroke = paintStroke;
	}
}
