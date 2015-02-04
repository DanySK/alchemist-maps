/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

//import java.io.File;

import java.io.File;

import org.mapsforge.map.reader.MapDatabase;

/**
 * A FileOpenResult is a simple DTO which is returned by
 * {@link MapDatabase#openFile(File)}.
 */
public class FileOpenResult {
	/**
	 * Singleton for a FileOpenResult instance with {@code success=true}.
	 */
	public static final FileOpenResult SUCCESS = new FileOpenResult();

	private final String errorMessage;
	private final boolean success;

	private FileOpenResult() {
		this.success = true;
		this.errorMessage = null;
	}

	/**
	 * @param errorMessage
	 *            a textual message describing the error, must not be null.
	 */
	public FileOpenResult(final String errorMessage) {
		if (errorMessage == null) {
			throw new IllegalArgumentException("error message must not be null");
		}

		this.success = false;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return a textual error description (might be null).
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * @return true if the file could be opened successfully, false otherwise.
	 */
	public boolean isSuccess() {
		return this.success;
	}
}
