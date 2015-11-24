/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import java.io.File;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.TileLayer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

public class TileRendererLayer extends TileLayer<RendererJob> {
    private final MapDatabase mapDatabase;
    private File mapFile;
    private final MapWorker mapWorker;
    private float textScale;
    private XmlRenderTheme xmlRenderTheme;

    public TileRendererLayer(final TileCache tileCache, final MapViewPosition mapViewPosition, final LayerManager layerManager, final GraphicFactory graphicFactory) {
        super(tileCache, mapViewPosition, graphicFactory);

        this.mapDatabase = new MapDatabase();
        final DatabaseRenderer databaseRenderer = new DatabaseRenderer(this.mapDatabase, graphicFactory);

        this.mapWorker = new MapWorker(tileCache, this.jobQueue, databaseRenderer, layerManager);
        this.mapWorker.start();

        this.textScale = 1;
    }

    @Override
    protected RendererJob createJob(final Tile tile) {
        return new RendererJob(tile, this.mapFile, this.xmlRenderTheme, this.textScale);
    }

    @Override
    public void destroy() {
        this.mapWorker.interrupt();
        this.mapDatabase.closeFile();

        super.destroy();
    }

    public File getMapFile() {
        return this.mapFile;
    }

    public float getTextScale() {
        return this.textScale;
    }

    public XmlRenderTheme getXmlRenderTheme() {
        return this.xmlRenderTheme;
    }

    public void setMapFile(final File mapFile) {
        this.mapFile = mapFile;
        // TODO fix this
        this.mapDatabase.openFile(mapFile);
    }

    public void setTextScale(final float textScale) {
        this.textScale = textScale;
    }

    public void setXmlRenderTheme(final XmlRenderTheme xmlRenderTheme) {
        this.xmlRenderTheme = xmlRenderTheme;
    }
}
