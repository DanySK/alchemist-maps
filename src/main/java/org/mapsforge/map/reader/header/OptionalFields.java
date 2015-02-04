/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

import org.mapsforge.core.model.CoordinatesUtil;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.reader.ReadBuffer;

public final class OptionalFields {
	/**
	 * Bitmask for the comment field in the file header.
	 */
	private static final int HEADER_BITMASK_COMMENT = 0x08;

	/**
	 * Bitmask for the created by field in the file header.
	 */
	private static final int HEADER_BITMASK_CREATED_BY = 0x04;

	/**
	 * Bitmask for the debug flag in the file header.
	 */
	private static final int HEADER_BITMASK_DEBUG = 0x80;

	/**
	 * Bitmask for the language preference field in the file header.
	 */
	private static final int HEADER_BITMASK_LANGUAGE_PREFERENCE = 0x10;

	/**
	 * Bitmask for the start position field in the file header.
	 */
	private static final int HEADER_BITMASK_START_POSITION = 0x40;

	/**
	 * Bitmask for the start zoom level field in the file header.
	 */
	private static final int HEADER_BITMASK_START_ZOOM_LEVEL = 0x20;

	/**
	 * The length of the language preference string.
	 */
	private static final int LANGUAGE_PREFERENCE_LENGTH = 2;

	/**
	 * Maximum valid start zoom level.
	 */
	private static final int START_ZOOM_LEVEL_MAX = 22;

	private String comment;

	private String createdBy;
	private final boolean hasComment;
	private final boolean hasCreatedBy;
	private final boolean hasLanguagePreference;
	private final boolean hasStartPosition;
	private final boolean hasStartZoomLevel;
	private final boolean isDebugFile;
	private String languagePreference;
	private LatLong startPosition;
	private Byte startZoomLevel;

	public static FileOpenResult readOptionalFields(final ReadBuffer readBuffer, final MapFileInfoBuilder mapFileInfoBuilder) {
		final OptionalFields optionalFields = new OptionalFields(readBuffer.readByte());
		mapFileInfoBuilder.setOptionalFields(optionalFields);

		final FileOpenResult fileOpenResult = optionalFields.readOptionalFields(readBuffer);
		if (!fileOpenResult.isSuccess()) {
			return fileOpenResult;
		}
		return FileOpenResult.SUCCESS;
	}

	private OptionalFields(final byte flags) {
		this.isDebugFile = (flags & HEADER_BITMASK_DEBUG) != 0;
		this.hasStartPosition = (flags & HEADER_BITMASK_START_POSITION) != 0;
		this.hasStartZoomLevel = (flags & HEADER_BITMASK_START_ZOOM_LEVEL) != 0;
		this.hasLanguagePreference = (flags & HEADER_BITMASK_LANGUAGE_PREFERENCE) != 0;
		this.hasComment = (flags & HEADER_BITMASK_COMMENT) != 0;
		this.hasCreatedBy = (flags & HEADER_BITMASK_CREATED_BY) != 0;
	}

	public String getComment() {
		return comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getLanguagePreference() {
		return languagePreference;
	}

	public LatLong getStartPosition() {
		return startPosition;
	}

	public Byte getStartZoomLevel() {
		return startZoomLevel;
	}

	public boolean isDebugFile() {
		return isDebugFile;
	}

	public boolean isHasComment() {
		return hasComment;
	}

	public boolean isHasCreatedBy() {
		return hasCreatedBy;
	}

	public boolean isHasLanguagePreference() {
		return hasLanguagePreference;
	}

	public boolean isHasStartPosition() {
		return hasStartPosition;
	}

	public boolean isHasStartZoomLevel() {
		return hasStartZoomLevel;
	}

	private FileOpenResult readLanguagePreference(final ReadBuffer readBuffer) {
		if (this.hasLanguagePreference) {
			final String countryCode = readBuffer.readUTF8EncodedString();
			if (countryCode.length() != LANGUAGE_PREFERENCE_LENGTH) {
				return new FileOpenResult("invalid language preference: " + countryCode);
			}
			this.languagePreference = countryCode;
		}
		return FileOpenResult.SUCCESS;
	}

	private FileOpenResult readMapStartPosition(final ReadBuffer readBuffer) {
		if (this.hasStartPosition) {
			final double mapStartLatitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());
			final double mapStartLongitude = CoordinatesUtil.microdegreesToDegrees(readBuffer.readInt());
			try {
				this.startPosition = new LatLong(mapStartLatitude, mapStartLongitude);
			} catch (final IllegalArgumentException e) {
				return new FileOpenResult(e.getMessage());
			}
		}
		return FileOpenResult.SUCCESS;
	}

	private FileOpenResult readMapStartZoomLevel(final ReadBuffer readBuffer) {
		if (this.hasStartZoomLevel) {
			// get and check the start zoom level (1 byte)
			final byte mapStartZoomLevel = readBuffer.readByte();
			if (mapStartZoomLevel < 0 || mapStartZoomLevel > START_ZOOM_LEVEL_MAX) {
				return new FileOpenResult("invalid map start zoom level: " + mapStartZoomLevel);
			}

			this.startZoomLevel = Byte.valueOf(mapStartZoomLevel);
		}
		return FileOpenResult.SUCCESS;
	}

	private FileOpenResult readOptionalFields(final ReadBuffer readBuffer) {
		FileOpenResult fileOpenResult = readMapStartPosition(readBuffer);
		if (!fileOpenResult.isSuccess()) {
			return fileOpenResult;
		}

		fileOpenResult = readMapStartZoomLevel(readBuffer);
		if (!fileOpenResult.isSuccess()) {
			return fileOpenResult;
		}

		fileOpenResult = readLanguagePreference(readBuffer);
		if (!fileOpenResult.isSuccess()) {
			return fileOpenResult;
		}

		if (this.hasComment) {
			this.comment = readBuffer.readUTF8EncodedString();
		}

		if (this.hasCreatedBy) {
			this.createdBy = readBuffer.readUTF8EncodedString();
		}

		return FileOpenResult.SUCCESS;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setLanguagePreference(final String languagePreference) {
		this.languagePreference = languagePreference;
	}

	public void setStartPosition(final LatLong startPosition) {
		this.startPosition = startPosition;
	}

	public void setStartZoomLevel(final Byte startZoomLevel) {
		this.startZoomLevel = startZoomLevel;
	}

}
