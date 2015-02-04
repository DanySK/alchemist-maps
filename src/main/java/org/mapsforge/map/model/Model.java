/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model;

import org.mapsforge.map.model.common.Persistable;
import org.mapsforge.map.model.common.PreferencesFacade;

public class Model implements Persistable {
	private final FrameBufferModel frameBufferModel = new FrameBufferModel();
	private final MapViewModel mapViewModel = new MapViewModel();
	private final MapViewPosition mapViewPosition = new MapViewPosition();

	public FrameBufferModel getFrameBufferModel() {
		return frameBufferModel;
	}

	public MapViewModel getMapViewModel() {
		return mapViewModel;
	}

	public MapViewPosition getMapViewPosition() {
		return mapViewPosition;
	}

	@Override
	public void init(final PreferencesFacade preferencesFacade) {
		this.mapViewPosition.init(preferencesFacade);
	}

	@Override
	public void save(final PreferencesFacade preferencesFacade) {
		this.mapViewPosition.save(preferencesFacade);
	}

}
