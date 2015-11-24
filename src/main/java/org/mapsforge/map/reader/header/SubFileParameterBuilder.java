/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

import org.mapsforge.core.model.BoundingBox;

public class SubFileParameterBuilder {
    private byte baseZoomLevel;
    private BoundingBox boundingBox;
    private long indexStartAddress;
    private long startAddress;
    private long subFileSize;
    private byte zoomLevelMax;
    private byte zoomLevelMin;

    public SubFileParameter build() {
        return new SubFileParameter(this);
    }

    public byte getBaseZoomLevel() {
        return baseZoomLevel;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public long getIndexStartAddress() {
        return indexStartAddress;
    }

    public long getStartAddress() {
        return startAddress;
    }

    public long getSubFileSize() {
        return subFileSize;
    }

    public byte getZoomLevelMax() {
        return zoomLevelMax;
    }

    public byte getZoomLevelMin() {
        return zoomLevelMin;
    }

    public void setBaseZoomLevel(final byte baseZoomLevel) {
        this.baseZoomLevel = baseZoomLevel;
    }

    public void setBoundingBox(final BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setIndexStartAddress(final long indexStartAddress) {
        this.indexStartAddress = indexStartAddress;
    }

    public void setStartAddress(final long startAddress) {
        this.startAddress = startAddress;
    }

    public void setSubFileSize(final long subFileSize) {
        this.subFileSize = subFileSize;
    }

    public void setZoomLevelMax(final byte zoomLevelMax) {
        this.zoomLevelMax = zoomLevelMax;
    }

    public void setZoomLevelMin(final byte zoomLevelMin) {
        this.zoomLevelMin = zoomLevelMin;
    }
}
