/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.mapsforge.map.model.MapViewModel;
import org.mapsforge.map.view.MapView;

public class MapViewComponentListener implements ComponentListener {
	private final MapView mapView;
	private final MapViewModel mapViewModel;

	public MapViewComponentListener(final MapView mapView, final MapViewModel mapViewModel) {
		this.mapView = mapView;
		this.mapViewModel = mapViewModel;
	}

	@Override
	public void componentHidden(final ComponentEvent componentEvent) {
		// do nothing
	}

	@Override
	public void componentMoved(final ComponentEvent componentEvent) {
		// do nothing
	}

	@Override
	public void componentResized(final ComponentEvent componentEvent) {
		final Dimension size = ((Component) this.mapView).getSize();
		this.mapViewModel.setDimension(new org.mapsforge.core.model.Dimension(size.width, size.height));
	}

	@Override
	public void componentShown(final ComponentEvent componentEvent) {
		// do nothing
	}
}
