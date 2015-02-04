/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.controller;

import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.Observer;

public class LayerManagerController implements Observer {
	private final LayerManager layerManager;
	private final Model model;

	public LayerManagerController(final LayerManager layerManager, final Model model) {
		this.layerManager = layerManager;
		this.model = model;

		model.getMapViewModel().addObserver(this);
		model.getMapViewPosition().addObserver(this);
	}
	
	public void dispose() {
		model.getMapViewModel().removeObserver(this);
		model.getMapViewPosition().removeObserver(this);
	}

	@Override
	public void onChange() {
		this.layerManager.redrawLayers();
	}
}
