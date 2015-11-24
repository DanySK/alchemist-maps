/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model.common;

public interface PreferencesFacade {
    void clear();

    boolean getBoolean(String key, boolean defaultValue);

    byte getByte(String key, byte defaultValue);

    double getDouble(String key, double defaultValue);

    float getFloat(String key, float defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    String getString(String key, String defaultValue);

    void putBoolean(String key, boolean value);

    void putByte(String key, byte value);

    void putDouble(String key, double value);

    void putFloat(String key, float value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putString(String key, String value);

    void save();
}
