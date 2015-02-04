/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;

public class MouseEventListener implements MouseListener, MouseMotionListener, MouseWheelListener {
	private Point lastDragPoint;
	private final MapViewPosition mapViewPosition;

	public MouseEventListener(final Model model) {
		this.mapViewPosition = model.getMapViewPosition();
	}

	@Override
	public void mouseClicked(final MouseEvent mouseEvent) {
		// do nothing
	}

	@Override
	public void mouseDragged(final MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			final Point point = mouseEvent.getPoint();
			if (this.lastDragPoint != null) {
				final int moveHorizontal = point.x - this.lastDragPoint.x;
				final int moveVertical = point.y - this.lastDragPoint.y;
				this.mapViewPosition.moveCenter(moveHorizontal, moveVertical);
			}
			this.lastDragPoint = point;
		}
	}

	@Override
	public void mouseEntered(final MouseEvent mouseEvent) {
		// do nothing
	}

	@Override
	public void mouseExited(final MouseEvent mouseEvent) {
		// do nothing
	}

	@Override
	public void mouseMoved(final MouseEvent mouseEvent) {
		// do nothing
	}

	@Override
	public void mousePressed(final MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			this.lastDragPoint = mouseEvent.getPoint();
		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
			this.mapViewPosition.setMapPosition(new MapPosition(new LatLong(48.22885564893414, 16.364897946404852), (byte) 20));
		}
	}

	@Override
	public void mouseReleased(final MouseEvent mouseEvent) {
		this.lastDragPoint = null;
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
		final byte zoomLevelDiff = (byte) -mouseWheelEvent.getWheelRotation();
		this.mapViewPosition.zoom(zoomLevelDiff);
	}
}
