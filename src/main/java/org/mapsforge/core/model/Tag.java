/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.model;

import java.io.Serializable;

import org.danilopianini.lang.HashUtils;

/**
 * A tag represents an immutable key-value pair.
 * 
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = -8646023524827250939L;
    private static final char KEY_VALUE_SEPARATOR = '=';
    private static final int EXTIMED_SIZE = 50;

    private final String key;
    private final String value;
    private int hash;

    /**
     * @param tag
     *            the textual representation of the tag.
     */
    public Tag(final String tag) {
        this(tag, tag.indexOf(KEY_VALUE_SEPARATOR));
    }

    private Tag(final String tag, final int splitPosition) {
        this(tag.substring(0, splitPosition), tag.substring(splitPosition + 1));
    }

    /**
     * @param k
     *            the key of the tag.
     * @param v
     *            the value of the tag.
     */
    public Tag(final String k, final String v) {
        this.key = k;
        this.value = v;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Tag)) {
            return false;
        }
        final Tag other = (Tag) obj;
        return key == null && other.key == null && value == null && other.value == null || key != null && key.equals(other.key) && value != null && value.equals(other.value);
    }

    /**
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = HashUtils.djb2int32obj(key, value);
        }
        return hash;
    }

    @Override
    public String toString() {
        // final int EXTIMED_SIZE = 50;
        final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
        stringBuilder.append("key=");
        stringBuilder.append(this.key);
        stringBuilder.append(", value=");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }
}
