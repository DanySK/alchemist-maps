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
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Tag;
import org.mapsforge.map.reader.MapDatabase;

/**
 * Contains the immutable metadata of a map file.
 * 
 * @see MapDatabase#getMapFileInfo()
 */
public class MapFileInfo {
	/**
	 * The bounding box of the map file.
	 */
	private final BoundingBox boundingBox;

	/**
	 * The comment field of the map file (may be null).
	 */
	private final String comment;

	/**
	 * The created by field of the map file (may be null).
	 */
	private final String createdBy;

	/**
	 * True if the map file includes debug information, false otherwise.
	 */
	private final boolean debugFile;

	/**
	 * The size of the map file, measured in bytes.
	 */
	private final long fileSize;

	/**
	 * The file version number of the map file.
	 */
	private final int fileVersion;

	/**
	 * The preferred language for names as defined in ISO 3166-1 (may be null).
	 */
	private final String languagePreference;

	/**
	 * The date of the map data in milliseconds since January 1, 1970.
	 */
	private final long mapDate;

	/**
	 * The number of sub-files in the map file.
	 */
	private final byte numberOfSubFiles;

	/**
	 * The POI tags.
	 */
	private final Tag[] poiTags;

	/**
	 * The name of the projection used in the map file.
	 */
	private final String projectionName;

	/**
	 * The map start position from the file header (may be null).
	 */
	private final LatLong startPosition;

	/**
	 * The map start zoom level from the file header (may be null).
	 */
	private final Byte startZoomLevel;

	/**
	 * The size of the tiles in pixels.
	 */
	private final int tilePixelSize;

	/**
	 * The way tags.
	 */
	private final Tag[] wayTags;

	public MapFileInfo(final MapFileInfoBuilder mapFileInfoBuilder) {
		this.comment = mapFileInfoBuilder.getOptionalFields().getComment();
		this.createdBy = mapFileInfoBuilder.getOptionalFields().getCreatedBy();
		this.debugFile = mapFileInfoBuilder.getOptionalFields().isDebugFile();
		this.fileSize = mapFileInfoBuilder.getFileSize();
		this.fileVersion = mapFileInfoBuilder.getFileVersion();
		this.languagePreference = mapFileInfoBuilder.getOptionalFields().getLanguagePreference();
		this.boundingBox = mapFileInfoBuilder.getBoundingBox();
		this.mapDate = mapFileInfoBuilder.getMapDate();
		this.numberOfSubFiles = mapFileInfoBuilder.getNumberOfSubFiles();
		this.poiTags = mapFileInfoBuilder.getPoiTags();
		this.projectionName = mapFileInfoBuilder.getProjectionName();
		this.startPosition = mapFileInfoBuilder.getOptionalFields().getStartPosition();
		this.startZoomLevel = mapFileInfoBuilder.getOptionalFields().getStartZoomLevel();
		this.tilePixelSize = mapFileInfoBuilder.getTilePixelSize();
		this.wayTags = mapFileInfoBuilder.getPoiTags();
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public String getComment() {
		return comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public long getFileSize() {
		return fileSize;
	}

	public int getFileVersion() {
		return fileVersion;
	}

	public String getLanguagePreference() {
		return languagePreference;
	}

	public long getMapDate() {
		return mapDate;
	}

	public byte getNumberOfSubFiles() {
		return numberOfSubFiles;
	}

	public Tag[] getPoiTags() {
		return poiTags;
	}

	public String getProjectionName() {
		return projectionName;
	}

	public LatLong getStartPosition() {
		return startPosition;
	}

	public Byte getStartZoomLevel() {
		return startZoomLevel;
	}

	public int getTilePixelSize() {
		return tilePixelSize;
	}

	public Tag[] getWayTags() {
		return wayTags;
	}

	public boolean isDebugFile() {
		return debugFile;
	}
}
