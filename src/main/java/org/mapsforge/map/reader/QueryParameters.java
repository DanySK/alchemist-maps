/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

public class QueryParameters {
	private long fromBaseTileX;
	private long fromBaseTileY;
	private long fromBlockX;
	private long fromBlockY;
	private int queryTileBitmask;
	private int queryZoomLevel;
	private long toBaseTileX;
	private long toBaseTileY;
	private long toBlockX;
	private long toBlockY;
	private boolean useTileBitmask;

	public long getFromBaseTileX() {
		return fromBaseTileX;
	}

	public long getFromBaseTileY() {
		return fromBaseTileY;
	}

	public long getFromBlockX() {
		return fromBlockX;
	}

	public long getFromBlockY() {
		return fromBlockY;
	}

	public int getQueryTileBitmask() {
		return queryTileBitmask;
	}

	public int getQueryZoomLevel() {
		return queryZoomLevel;
	}

	public long getToBaseTileX() {
		return toBaseTileX;
	}

	public long getToBaseTileY() {
		return toBaseTileY;
	}

	public long getToBlockX() {
		return toBlockX;
	}

	public long getToBlockY() {
		return toBlockY;
	}

	public boolean isUseTileBitmask() {
		return useTileBitmask;
	}

	public void setFromBaseTileX(final long fromBaseTileX) {
		this.fromBaseTileX = fromBaseTileX;
	}

	public void setFromBaseTileY(final long fromBaseTileY) {
		this.fromBaseTileY = fromBaseTileY;
	}

	public void setFromBlockX(final long fromBlockX) {
		this.fromBlockX = fromBlockX;
	}

	public void setFromBlockY(final long fromBlockY) {
		this.fromBlockY = fromBlockY;
	}

	public void setQueryTileBitmask(final int queryTileBitmask) {
		this.queryTileBitmask = queryTileBitmask;
	}

	public void setQueryZoomLevel(final int queryZoomLevel) {
		this.queryZoomLevel = queryZoomLevel;
	}

	public void setToBaseTileX(final long toBaseTileX) {
		this.toBaseTileX = toBaseTileX;
	}

	public void setToBaseTileY(final long toBaseTileY) {
		this.toBaseTileY = toBaseTileY;
	}

	public void setToBlockX(final long toBlockX) {
		this.toBlockX = toBlockX;
	}

	public void setToBlockY(final long toBlockY) {
		this.toBlockY = toBlockY;
	}

	public void setUseTileBitmask(final boolean useTileBitmask) {
		this.useTileBitmask = useTileBitmask;
	}
}
