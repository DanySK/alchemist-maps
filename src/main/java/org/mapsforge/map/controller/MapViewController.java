/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.controller;

import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.Observer;
import org.mapsforge.map.view.MapView;

public class MapViewController implements Observer {
	private final MapView mapView;
	private final Model model;

	public MapViewController(final MapView mapView, final Model model) {
		this.mapView = mapView;
		this.model = model;

		model.getMapViewPosition().addObserver(this);
	}
	
	public void dispose() {
		model.getMapViewPosition().removeObserver(this);
	}

	@Override
	public void onChange() {
		this.mapView.repaint();
	}
}
