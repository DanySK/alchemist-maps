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

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.queue.Job;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

public class RendererJob extends Job {
    private final File mapFile;
    private final float textScale;
    private final XmlRenderTheme xmlRenderTheme;
    private static final int PRIME = 31;

    public RendererJob(final Tile tile, final File mapFile, final XmlRenderTheme xmlRenderTheme, final float textScale) {
        super(tile);

        if (mapFile == null) {
            throw new IllegalArgumentException("mapFile must not be null");
        } else if (xmlRenderTheme == null) {
            throw new IllegalArgumentException("xmlRenderTheme must not be null");
        } else if (textScale <= 0 || Float.isNaN(textScale)) {
            throw new IllegalArgumentException("invalid textScale: " + textScale);
        }

        this.mapFile = mapFile;
        this.xmlRenderTheme = xmlRenderTheme;
        this.textScale = textScale;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (!(obj instanceof RendererJob)) {
            return false;
        }
        final RendererJob other = (RendererJob) obj;
        if (!this.mapFile.equals(other.mapFile)) {
            return false;
        } else if (Float.floatToIntBits(this.textScale) != Float.floatToIntBits(other.textScale)) {
            return false;
        } else if (!this.xmlRenderTheme.equals(other.xmlRenderTheme)) {
            return false;
        }
        return true;
    }

    public File getMapFile() {
        return mapFile;
    }

    public float getTextScale() {
        return textScale;
    }

    public XmlRenderTheme getXmlRenderTheme() {
        return xmlRenderTheme;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = PRIME * result + this.mapFile.hashCode();
        result = PRIME * result + Float.floatToIntBits(this.textScale);
        result = PRIME * result + this.xmlRenderTheme.hashCode();
        return result;
    }
}
