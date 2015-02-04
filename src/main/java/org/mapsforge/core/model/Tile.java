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
 * A tile represents a rectangular part of the world map. All tiles can be
 * identified by their X and Y number together with their zoom level. The actual
 * area that a tile covers on a map depends on the underlying map projection.
 * 
 * @author Mapsforge
 * @author Giovanni Ciatto
 * @author Danilo Pianini *
 */
/**
 * @author danysk
 *
 */
public class Tile implements Serializable {
	
	private static final long serialVersionUID = -4406366144776978628L;
	/**
	 * Width and height of a map tile in pixel.
	 */
	public static final int TILE_SIZE = 256;
	private static final int EXTIMED_SIZE = 100;
	
	private final long tileX;
	private final long tileY;
	private final byte zoomLevel;
	
	private int hash;

	/**
	 * @param tX
	 *            the X number of the tile.
	 * @param tY
	 *            the Y number of the tile.
	 * @param zoom
	 *            the zoom level of the tile.
	 */
	public Tile(final long tX, final long tY, final byte zoom) {
		if (tX < 0) {
			throw new IllegalArgumentException("tileX must not be negative: " + tX);
		} else if (tY < 0) {
			throw new IllegalArgumentException("tileY must not be negative: " + tY);
		} else if (zoom < 0) {
			throw new IllegalArgumentException("zoomLevel must not be negative: " + zoom);
		}

		final double maxTileNumber = Math.pow(2, zoom) - 1;
		if (tX > maxTileNumber) {
			throw new IllegalArgumentException("invalid tileX number: " + tX);
		} else if (tY > maxTileNumber) {
			throw new IllegalArgumentException("invalid tileY number: " + tY);
		}

		this.tileX = tX;
		this.tileY = tY;
		this.zoomLevel = zoom;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Tile)) {
			return false;
		}
		final Tile other = (Tile) obj;
		return tileX == other.tileX && tileY == other.tileY && zoomLevel == other.zoomLevel;
	}

	/**
	 * @return the parent tile of this tile or null, if the zoom level of this
	 *         tile is 0.
	 */
	public Tile getParent() {
		if (this.zoomLevel == 0) {
			return null;
		}
		return new Tile(this.tileX / 2, this.tileY / 2, (byte) (this.zoomLevel - 1));
	}

	/**
	 * @param otherTile other tile
	 * @return shift x
	 */
	public long getShiftX(final Tile otherTile) {
		if (this.equals(otherTile)) {
			return 0;
		}

		return this.tileX % 2 + 2 * getParent().getShiftX(otherTile);
	}

	/**
	 * @param otherTile other tile
	 * @return shift y
	 */
	public long getShiftY(final Tile otherTile) {
		if (this.equals(otherTile)) {
			return 0;
		}

		return this.tileY % 2 + 2 * getParent().getShiftY(otherTile);
	}

	/**
	 * @return tile x
	 */
	public long getTileX() {
		return tileX;
	}

	/**
	 * @return tile y
	 */
	public long getTileY() {
		return tileY;
	}

	/**
	 * @return zoom level
	 */
	public byte getZoomLevel() {
		return zoomLevel;
	}

	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = HashUtils.djb2int32(tileX, tileY, zoomLevel);
		}
		return hash;
	}

	@Override
	public String toString() {
		// final int EXTIMED_SIZE = 100;
		final StringBuilder stringBuilder = new StringBuilder(EXTIMED_SIZE);
		stringBuilder.append("tileX=");
		stringBuilder.append(this.tileX);
		stringBuilder.append(", tileY=");
		stringBuilder.append(this.tileY);
		stringBuilder.append(", zoomLevel=");
		stringBuilder.append(this.zoomLevel);
		return stringBuilder.toString();
	}
}
