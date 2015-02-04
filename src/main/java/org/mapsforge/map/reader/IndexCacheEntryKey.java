/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

import org.mapsforge.map.reader.header.SubFileParameter;

/**
 * An immutable container class which is the key for the index cache.
 */
public class IndexCacheEntryKey {
	private final int hashCodeValue;
	private final long indexBlockNumber;
	private final SubFileParameter subFileParameter;

	/**
	 * Creates an immutable key to be stored in a map.
	 * 
	 * @param subFileParameter
	 *            the parameters of the map file.
	 * @param indexBlockNumber
	 *            the number of the index block.
	 */
	public IndexCacheEntryKey(final SubFileParameter subFileParameter, final long indexBlockNumber) {
		this.subFileParameter = subFileParameter;
		this.indexBlockNumber = indexBlockNumber;
		this.hashCodeValue = calculateHashCode();
	}

	/**
	 * @return the hash code of this object.
	 */
	private int calculateHashCode() {
		int result = 7;
		result = 31 * result + (this.subFileParameter == null ? 0 : this.subFileParameter.hashCode());
		result = 31 * result + (int) (this.indexBlockNumber ^ this.indexBlockNumber >>> 32);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof IndexCacheEntryKey)) {
			return false;
		}
		final IndexCacheEntryKey other = (IndexCacheEntryKey) obj;
		if (this.subFileParameter == null && other.subFileParameter != null) {
			return false;
		} else if (this.subFileParameter != null && !this.subFileParameter.equals(other.subFileParameter)) {
			return false;
		} else if (this.indexBlockNumber != other.indexBlockNumber) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.hashCodeValue;
	}
}
