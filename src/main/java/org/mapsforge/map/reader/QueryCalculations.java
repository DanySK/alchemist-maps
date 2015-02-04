/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.reader.header.SubFileParameter;

public final class QueryCalculations {
	public static void calculateBaseTiles(final QueryParameters queryParameters, final Tile tile, final SubFileParameter subFileParameter) {
		if (tile.getZoomLevel() < subFileParameter.getBaseZoomLevel()) {
			// calculate the XY numbers of the upper left and lower right
			// sub-tiles
			final int zoomLevelDifference = subFileParameter.getBaseZoomLevel() - tile.getZoomLevel();
			queryParameters.setFromBaseTileX(tile.getTileX() << zoomLevelDifference);
			queryParameters.setFromBaseTileY(tile.getTileY() << zoomLevelDifference);
			queryParameters.setToBaseTileX(queryParameters.getFromBaseTileX() + (1 << zoomLevelDifference) - 1);
			queryParameters.setToBaseTileY(queryParameters.getFromBaseTileY() + (1 << zoomLevelDifference) - 1);
			queryParameters.setUseTileBitmask(false);
		} else if (tile.getZoomLevel() > subFileParameter.getBaseZoomLevel()) {
			// calculate the XY numbers of the parent base tile
			final int zoomLevelDifference = tile.getZoomLevel() - subFileParameter.getBaseZoomLevel();
			queryParameters.setFromBaseTileX(tile.getTileX() >>> zoomLevelDifference);
			queryParameters.setFromBaseTileY(tile.getTileY() >>> zoomLevelDifference);
			queryParameters.setToBaseTileX(queryParameters.getFromBaseTileX());
			queryParameters.setToBaseTileY(queryParameters.getFromBaseTileY());
			queryParameters.setUseTileBitmask(false);
			queryParameters.setQueryTileBitmask(calculateTileBitmask(tile, zoomLevelDifference));
		} else {
			// use the tile XY numbers of the requested tile
			queryParameters.setFromBaseTileX(tile.getTileX());
			queryParameters.setFromBaseTileY(tile.getTileY());
			queryParameters.setToBaseTileX(queryParameters.getFromBaseTileX());
			queryParameters.setToBaseTileY(queryParameters.getFromBaseTileY());
			queryParameters.setUseTileBitmask(false);
		}
	}

	public static void calculateBlocks(final QueryParameters queryParameters, final SubFileParameter subFileParameter) {
		// calculate the blocks in the file which need to be read
		queryParameters.setFromBlockX(Math.max(queryParameters.getFromBaseTileX() - subFileParameter.getBoundaryTileLeft(), 0));
		queryParameters.setFromBlockY(Math.max(queryParameters.getFromBaseTileY() - subFileParameter.getBoundaryTileTop(), 0));
		queryParameters.setToBlockX(Math.min(queryParameters.getToBaseTileX() - subFileParameter.getBoundaryTileLeft(), subFileParameter.getBlocksWidth() - 1));
		queryParameters.setToBlockY(Math.min(queryParameters.getToBaseTileY() - subFileParameter.getBoundaryTileTop(), subFileParameter.getBlocksHeight() - 1));
	}

	public static int calculateTileBitmask(final Tile tile, final int zoomLevelDifference) {
		if (zoomLevelDifference == 1) {
			return getFirstLevelTileBitmask(tile);
		}

		// calculate the XY numbers of the second level sub-tile
		final long subtileX = tile.getTileX() >>> zoomLevelDifference - 2;
		final long subtileY = tile.getTileY() >>> zoomLevelDifference - 2;

		// calculate the XY numbers of the parent tile
		final long parentTileX = subtileX >>> 1;
		final long parentTileY = subtileY >>> 1;

		// determine the correct bitmask for all 16 sub-tiles
		if (parentTileX % 2 == 0 && parentTileY % 2 == 0) {
			return getSecondLevelTileBitmaskUpperLeft(subtileX, subtileY);
		} else if (parentTileX % 2 == 1 && parentTileY % 2 == 0) {
			return getSecondLevelTileBitmaskUpperRight(subtileX, subtileY);
		} else if (parentTileX % 2 == 0 && parentTileY % 2 == 1) {
			return getSecondLevelTileBitmaskLowerLeft(subtileX, subtileY);
		} else {
			return getSecondLevelTileBitmaskLowerRight(subtileX, subtileY);
		}
	}

	private static int getFirstLevelTileBitmask(final Tile tile) {
		if (tile.getTileX() % 2 == 0 && tile.getTileY() % 2 == 0) {
			// upper left quadrant
			return 0xcc00;
		} else if (tile.getTileX() % 2 == 1 && tile.getTileY() % 2 == 0) {
			// upper right quadrant
			return 0x3300;
		} else if (tile.getTileX() % 2 == 0 && tile.getTileY() % 2 == 1) {
			// lower left quadrant
			return 0xcc;
		} else {
			// lower right quadrant
			return 0x33;
		}
	}

	private static int getSecondLevelTileBitmaskLowerLeft(final long subtileX, final long subtileY) {
		if (subtileX % 2 == 0 && subtileY % 2 == 0) {
			// upper left sub-tile
			return 0x80;
		} else if (subtileX % 2 == 1 && subtileY % 2 == 0) {
			// upper right sub-tile
			return 0x40;
		} else if (subtileX % 2 == 0 && subtileY % 2 == 1) {
			// lower left sub-tile
			return 0x8;
		} else {
			// lower right sub-tile
			return 0x4;
		}
	}

	private static int getSecondLevelTileBitmaskLowerRight(final long subtileX, final long subtileY) {
		if (subtileX % 2 == 0 && subtileY % 2 == 0) {
			// upper left sub-tile
			return 0x20;
		} else if (subtileX % 2 == 1 && subtileY % 2 == 0) {
			// upper right sub-tile
			return 0x10;
		} else if (subtileX % 2 == 0 && subtileY % 2 == 1) {
			// lower left sub-tile
			return 0x2;
		} else {
			// lower right sub-tile
			return 0x1;
		}
	}

	private static int getSecondLevelTileBitmaskUpperLeft(final long subtileX, final long subtileY) {
		if (subtileX % 2 == 0 && subtileY % 2 == 0) {
			// upper left sub-tile
			return 0x8000;
		} else if (subtileX % 2 == 1 && subtileY % 2 == 0) {
			// upper right sub-tile
			return 0x4000;
		} else if (subtileX % 2 == 0 && subtileY % 2 == 1) {
			// lower left sub-tile
			return 0x800;
		} else {
			// lower right sub-tile
			return 0x400;
		}
	}

	private static int getSecondLevelTileBitmaskUpperRight(final long subtileX, final long subtileY) {
		if (subtileX % 2 == 0 && subtileY % 2 == 0) {
			// upper left sub-tile
			return 0x2000;
		} else if (subtileX % 2 == 1 && subtileY % 2 == 0) {
			// upper right sub-tile
			return 0x1000;
		} else if (subtileX % 2 == 0 && subtileY % 2 == 1) {
			// lower left sub-tile
			return 0x200;
		} else {
			// lower right sub-tile
			return 0x100;
		}
	}

	private QueryCalculations() {
		throw new IllegalStateException();
	}
}
