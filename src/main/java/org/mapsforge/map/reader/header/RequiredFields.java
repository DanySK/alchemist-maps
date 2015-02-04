/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

import java.io.IOException;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.CoordinatesUtil;
import org.mapsforge.core.model.Tag;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.reader.ReadBuffer;

public final class RequiredFields {
	/**
	 * Magic byte at the beginning of a valid binary map file.
	 */
	private static final String BINARY_OSM_MAGIC_BYTE = "mapsforge binary OSM";

	/**
	 * Maximum size of the file header in bytes.
	 */
	private static final int HEADER_SIZE_MAX = 1000000;

	/**
	 * Minimum size of the file header in bytes.
	 */
	private static final int HEADER_SIZE_MIN = 70;

	/**
	 * The name of the Mercator projection as stored in the file header.
	 */
	private static final String MERCATOR = "Mercator";

	/**
	 * Version of the map file format which is supported by this implementation.
	 */
	private static final int SUPPORTED_FILE_VERSION = 3;

	public static FileOpenResult readBoundingBox(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		final double minLatitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());
		final double minLongitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());
		final double maxLatitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());
		final double maxLongitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());

		try {
			mapFileInfoBuilder.setBoundingBox(new BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude));
		} catch (final IllegalArgumentException e) {
			return new FileOpenResult(e.getMessage());
		}
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readFileSize(final ReadBuffer readBuffer, final long fileSize, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the file size (8 bytes)
		final long headerFileSize = readBuffer.readLong();
		if (headerFileSize != fileSize) {
			return new FileOpenResult("invalid file size: " + headerFileSize);
		}
		mapFileInfoBuilder.setFileSize(fileSize);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readFileVersion(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the file version (4 bytes)
		final int fileVersion = readBuffer.readInt();
		if (fileVersion != SUPPORTED_FILE_VERSION) {
			return new FileOpenResult("unsupported file version: " + fileVersion);
		}
		mapFileInfoBuilder.setFileVersion(fileVersion);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readMagicByte(final ReadBuffer readBuffer) throws IOException {
		// read the the magic byte and the file header size into the buffer
		final int magicByteLength = BINARY_OSM_MAGIC_BYTE.length();
		if (!readBuffer.readFromFile(magicByteLength + 4)) {
			return new FileOpenResult("reading magic byte has failed");
		}

		// get and check the magic byte
		final String magicByte = readBuffer.readUTF8EncodedString(magicByteLength);
		if (!BINARY_OSM_MAGIC_BYTE.equals(magicByte)) {
			return new FileOpenResult("invalid magic byte: " + magicByte);
		}
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readMapDate(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the the map date (8 bytes)
		final long mapDate = readBuffer.readLong();
		// is the map date before 2010-01-10 ?
		if (mapDate < 1200000000000L) {
			return new FileOpenResult("invalid map date: " + mapDate);
		}
		mapFileInfoBuilder.setMapDate(mapDate);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readPoiTags(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the number of POI tags (2 bytes)
		final int numberOfPoiTags = readBuffer.readShort();
		if (numberOfPoiTags < 0) {
			return new FileOpenResult("invalid number of POI tags: " + numberOfPoiTags);
		}

		final Tag[] poiTags = new Tag[numberOfPoiTags];
		for (int currentTagId = 0; currentTagId < numberOfPoiTags; ++currentTagId) {
			// get and check the POI tag
			final String tag = readBuffer.readUTF8EncodedString();
			if (tag == null) {
				return new FileOpenResult("POI tag must not be null: " + currentTagId);
			}
			poiTags[currentTagId] = new Tag(tag);
		}
		mapFileInfoBuilder.setPoiTags(poiTags);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readProjectionName(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the projection name
		final String projectionName = readBuffer.readUTF8EncodedString();
		if (!MERCATOR.equals(projectionName)) {
			return new FileOpenResult("unsupported projection: " + projectionName);
		}
		mapFileInfoBuilder.setProjectionName(projectionName);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readRemainingHeader(final ReadBuffer readBuffer) throws IOException {
		// get and check the size of the remaining file header (4 bytes)
		final int remainingHeaderSize = readBuffer.readInt();
		if (remainingHeaderSize < HEADER_SIZE_MIN || remainingHeaderSize > HEADER_SIZE_MAX) {
			return new FileOpenResult("invalid remaining header size: " + remainingHeaderSize);
		}

		// read the header data into the buffer
		if (!readBuffer.readFromFile(remainingHeaderSize)) {
			return new FileOpenResult("reading header data has failed: " + remainingHeaderSize);
		}
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readTilePixelSize(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the tile pixel size (2 bytes)
		final int tilePixelSize = readBuffer.readShort();
		if (tilePixelSize != Tile.TILE_SIZE) {
			return new FileOpenResult("unsupported tile pixel size: " + tilePixelSize);
		}
		mapFileInfoBuilder.setTilePixelSize(tilePixelSize);
		return FileOpenResult.SUCCESS;
	}

	public static FileOpenResult readWayTags(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		// get and check the number of way tags (2 bytes)
		final int numberOfWayTags = readBuffer.readShort();
		if (numberOfWayTags < 0) {
			return new FileOpenResult("invalid number of way tags: " + numberOfWayTags);
		}

		final Tag[] wayTags = new Tag[numberOfWayTags];

		for (int currentTagId = 0; currentTagId < numberOfWayTags; ++currentTagId) {
			// get and check the way tag
			final String tag = readBuffer.readUTF8EncodedString();
			if (tag == null) {
				return new FileOpenResult("way tag must not be null: " + currentTagId);
			}
			wayTags[currentTagId] = new Tag(tag);
		}
		mapFileInfoBuilder.setWayTags(wayTags);
		return FileOpenResult.SUCCESS;
	}

	private RequiredFields() {
		throw new IllegalStateException();
	}
}
