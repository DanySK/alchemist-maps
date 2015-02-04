/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing;

import java.awt.Component;
import java.awt.Graphics2D;
import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.JavaUtilPreferences;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.view.MainFrame;
import org.mapsforge.map.swing.view.MapView;
import org.mapsforge.map.swing.view.WindowCloseDialog;

public final class MapViewer {
	private static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;

	public static TileCache createLocalTileCache() {
		final TileCache firstLevelTileCache = new InMemoryTileCache(256);
		final File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "mapsforge");
		final TileCache secondLevelTileCache = new FileSystemTileCache(4096, cacheDirectory, GRAPHIC_FACTORY);
		return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
	}

	public static Component createMapView(final MapView mapView, final Model model, final File map) {
		mapView.addComponentListener(new MapViewComponentListener(mapView, model.getMapViewModel()));

		final LayerManager layerManager = mapView.getLayerManager();
		final List<Layer> layers = layerManager.getLayers();
		final TileCache tileCache = createLocalTileCache();

		if (map != null && map.exists()) {
			layers.add(createTileRendererLayer(tileCache, model.getMapViewPosition(), layerManager, map));
		} else {
			layers.add(createTileDownloadLayer(tileCache, model.getMapViewPosition(), layerManager));
		}
		return mapView;
	}

	public static Component createMapView(final Model model, final File map) {
		final MapView mapView = new MapView(model) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7464396061886399840L;

			@Override
			public void drawOnMap(final Graphics2D g) {

			}
		};
		return createMapView(mapView, model, map);
	}

	public static Layer createTileDownloadLayer(final TileCache tileCache, final MapViewPosition mapViewPosition, final LayerManager layerManager) {

		final TileSource tileSource = OpenStreetMapMapnik.INSTANCE;
		return new TileDownloadLayer(tileCache, mapViewPosition, tileSource, layerManager, GRAPHIC_FACTORY);
	}

	public static Layer createTileRendererLayer(final TileCache tileCache, final MapViewPosition mapViewPosition, final LayerManager layerManager, final File map) {
		final TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapViewPosition, layerManager, GRAPHIC_FACTORY);
		tileRendererLayer.setMapFile(map);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		return tileRendererLayer;
	}

	public static void main(final String[] args) {
		final Model model = new Model();
		final MapView mv = (MapView) createMapView(model, null);
		final PreferencesFacade preferencesFacade = new JavaUtilPreferences(Preferences.userNodeForPackage(MapViewer.class));
		model.init(preferencesFacade);

		final MouseEventListener mouseEventListener = new MouseEventListener(model);
		mv.addMouseListener(mouseEventListener);
		mv.addMouseMotionListener(mouseEventListener);
		mv.addMouseWheelListener(mouseEventListener);

		final MainFrame mainFrame = new MainFrame();
		mainFrame.addWindowListener(new WindowCloseDialog(mainFrame, model, preferencesFacade));
		mainFrame.add(mv);

		mainFrame.setVisible(true);
	}

	private MapViewer() {
		throw new IllegalStateException();
	}
}
