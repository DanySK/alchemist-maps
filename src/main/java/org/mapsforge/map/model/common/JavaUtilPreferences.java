/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model.common;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class JavaUtilPreferences implements PreferencesFacade {
	private final Preferences preferences;

	public JavaUtilPreferences(final Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public synchronized void clear() {
		try {
			this.preferences.clear();
		} catch (final BackingStoreException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public synchronized boolean getBoolean(final String key, final boolean defaultValue) {
		return this.preferences.getBoolean(key, defaultValue);
	}

	@Override
	public synchronized byte getByte(final String key, final byte defaultValue) {
		final int intValue = this.preferences.getInt(key, defaultValue);
		if (intValue < Byte.MIN_VALUE || intValue > Byte.MAX_VALUE) {
			throw new IllegalStateException("byte value out of range: " + intValue);
		}
		return (byte) intValue;
	}

	@Override
	public synchronized double getDouble(final String key, final double defaultValue) {
		return this.preferences.getDouble(key, defaultValue);
	}

	@Override
	public synchronized float getFloat(final String key, final float defaultValue) {
		return this.preferences.getFloat(key, defaultValue);
	}

	@Override
	public synchronized int getInt(final String key, final int defaultValue) {
		return this.preferences.getInt(key, defaultValue);
	}

	@Override
	public synchronized long getLong(final String key, final long defaultValue) {
		return this.preferences.getLong(key, defaultValue);
	}

	@Override
	public synchronized String getString(final String key, final String defaultValue) {
		return this.preferences.get(key, defaultValue);
	}

	@Override
	public synchronized void putBoolean(final String key, final boolean value) {
		this.preferences.putBoolean(key, value);
	}

	@Override
	public synchronized void putByte(final String key, final byte value) {
		this.preferences.putInt(key, value);
	}

	@Override
	public synchronized void putDouble(final String key, final double value) {
		this.preferences.putDouble(key, value);
	}

	@Override
	public synchronized void putFloat(final String key, final float value) {
		this.preferences.putFloat(key, value);
	}

	@Override
	public synchronized void putInt(final String key, final int value) {
		this.preferences.putInt(key, value);
	}

	@Override
	public synchronized void putLong(final String key, final long value) {
		this.preferences.putLong(key, value);
	}

	@Override
	public synchronized void putString(final String key, final String value) {
		this.preferences.put(key, value);
	}

	@Override
	public synchronized void save() {
		try {
			this.preferences.flush();
		} catch (final BackingStoreException e) {
			throw new IllegalStateException(e);
		}
	}
}
