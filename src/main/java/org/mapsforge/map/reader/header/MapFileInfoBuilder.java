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
import org.mapsforge.core.model.Tag;

public class MapFileInfoBuilder {
    private BoundingBox boundingBox;
    private long fileSize;
    private int fileVersion;
    private long mapDate;
    private byte numberOfSubFiles;
    private OptionalFields optionalFields;
    private Tag[] poiTags;
    private String projectionName;
    private int tilePixelSize;
    private Tag[] wayTags;

    public MapFileInfo build() {
        return new MapFileInfo(this);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public long getMapDate() {
        return mapDate;
    }

    public byte getNumberOfSubFiles() {
        return numberOfSubFiles;
    }

    public OptionalFields getOptionalFields() {
        return optionalFields;
    }

    public Tag[] getPoiTags() {
        return poiTags;
    }

    public String getProjectionName() {
        return projectionName;
    }

    public int getTilePixelSize() {
        return tilePixelSize;
    }

    public Tag[] getWayTags() {
        return wayTags;
    }

    public void setBoundingBox(final BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setFileSize(final long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileVersion(final int fileVersion) {
        this.fileVersion = fileVersion;
    }

    public void setMapDate(final long mapDate) {
        this.mapDate = mapDate;
    }

    public void setNumberOfSubFiles(final byte numberOfSubFiles) {
        this.numberOfSubFiles = numberOfSubFiles;
    }

    public void setOptionalFields(final OptionalFields optionalFields) {
        this.optionalFields = optionalFields;
    }

    public void setPoiTags(final Tag[] poiTags) {
        this.poiTags = poiTags;
    }

    public void setProjectionName(final String projectionName) {
        this.projectionName = projectionName;
    }

    public void setTilePixelSize(final int tilePixelSize) {
        this.tilePixelSize = tilePixelSize;
    }

    public void setWayTags(final Tag[] wayTags) {
        this.wayTags = wayTags;
    }
}
