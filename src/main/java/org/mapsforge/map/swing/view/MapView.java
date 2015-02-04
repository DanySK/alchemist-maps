/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing.view;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.controller.FrameBufferController;
import org.mapsforge.map.controller.LayerManagerController;
import org.mapsforge.map.controller.MapViewController;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.view.FrameBuffer;

public abstract class MapView extends Container implements org.mapsforge.map.view.MapView {
	private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
	private static final long serialVersionUID = 1L;

	private final FrameBuffer frameBuffer;
	private final LayerManager layerManager;
	private final FrameBufferController fBController;
	private final LayerManagerController lMController;
	private final MapViewController mVController;

	public MapView(final Model model) {
		super();
		
		this.frameBuffer = new FrameBuffer(model.getFrameBufferModel(), GRAPHIC_FACTORY);
		fBController = new FrameBufferController(this.frameBuffer, model);

		this.layerManager = new LayerManager(this, model.getMapViewPosition(), GRAPHIC_FACTORY);
		this.layerManager.start();
		lMController = new LayerManagerController(this.layerManager, model);

		mVController = new MapViewController(this, model);
	}
	
	public void dispose() {
		fBController.dispose();
		lMController.dispose();
		mVController.dispose();
	}

	private void drawMap(final Graphics graphics) {
		if (graphics instanceof Graphics2D) {
			this.frameBuffer.draw(AwtGraphicFactory.createCanvas((Graphics2D) graphics));
		} else {
			throw new IllegalArgumentException("unexpected type: " + graphics);
		}

		drawOnMap((Graphics2D) graphics);
	}

	public abstract void drawOnMap(Graphics2D g);

	@Override
	public FrameBuffer getFrameBuffer() {
		return this.frameBuffer;
	}

	@Override
	public LayerManager getLayerManager() {
		return this.layerManager;
	}

	@Override
	public void paint(final Graphics graphics) {
		drawMap(graphics);
		super.paint(graphics);
	}
}
