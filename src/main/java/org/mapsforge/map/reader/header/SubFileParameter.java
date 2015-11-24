/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

import org.mapsforge.core.util.MercatorProjection;

/**
 * Holds all parameters of a sub-file.
 */
public class SubFileParameter {
    /**
     * Number of bytes a single index entry consists of.
     */
    public static final byte BYTES_PER_INDEX_ENTRY = 5;

    /**
     * Base zoom level of the sub-file, which equals to one block.
     */
    private final byte baseZoomLevel;

    /**
     * Vertical amount of blocks in the grid.
     */
    private final long blocksHeight;

    /**
     * Horizontal amount of blocks in the grid.
     */
    private final long blocksWidth;

    /**
     * Y number of the tile at the bottom boundary in the grid.
     */
    private final long boundaryTileBottom;

    /**
     * X number of the tile at the left boundary in the grid.
     */
    private final long boundaryTileLeft;

    /**
     * X number of the tile at the right boundary in the grid.
     */
    private final long boundaryTileRight;

    /**
     * Y number of the tile at the top boundary in the grid.
     */
    private final long boundaryTileTop;

    /**
     * Absolute end address of the index in the enclosing file.
     */
    private final long indexEndAddress;

    /**
     * Absolute start address of the index in the enclosing file.
     */
    private final long indexStartAddress;

    /**
     * Total number of blocks in the grid.
     */
    private final long numberOfBlocks;

    /**
     * Absolute start address of the sub-file in the enclosing file.
     */
    private final long startAddress;

    /**
     * Size of the sub-file in bytes.
     */
    private final long subFileSize;

    /**
     * Maximum zoom level for which the block entries tables are made.
     */
    private final byte zoomLevelMax;

    /**
     * Minimum zoom level for which the block entries tables are made.
     */
    private final byte zoomLevelMin;

    /**
     * Stores the hash code of this object.
     */
    private final int hashCodeValue;

    public SubFileParameter(final SubFileParameterBuilder subFileParameterBuilder) {
        this.startAddress = subFileParameterBuilder.getStartAddress();
        this.indexStartAddress = subFileParameterBuilder.getIndexStartAddress();
        this.subFileSize = subFileParameterBuilder.getSubFileSize();
        this.baseZoomLevel = subFileParameterBuilder.getBaseZoomLevel();
        this.zoomLevelMin = subFileParameterBuilder.getZoomLevelMin();
        this.zoomLevelMax = subFileParameterBuilder.getZoomLevelMax();
        this.hashCodeValue = calculateHashCode();

        // calculate the XY numbers of the boundary tiles in this sub-file
        this.boundaryTileBottom = MercatorProjection.latitudeToTileY(subFileParameterBuilder.getBoundingBox().getMinLatitude(), this.baseZoomLevel);
        this.boundaryTileLeft = MercatorProjection.longitudeToTileX(subFileParameterBuilder.getBoundingBox().getMinLongitude(), this.baseZoomLevel);
        this.boundaryTileTop = MercatorProjection.latitudeToTileY(subFileParameterBuilder.getBoundingBox().getMaxLatitude(), this.baseZoomLevel);
        this.boundaryTileRight = MercatorProjection.longitudeToTileX(subFileParameterBuilder.getBoundingBox().getMaxLongitude(), this.baseZoomLevel);

        // calculate the horizontal and vertical amount of blocks in this
        // sub-file
        this.blocksWidth = this.boundaryTileRight - this.boundaryTileLeft + 1;
        this.blocksHeight = this.boundaryTileBottom - this.boundaryTileTop + 1;

        // calculate the total amount of blocks in this sub-file
        this.numberOfBlocks = this.blocksWidth * this.blocksHeight;

        this.indexEndAddress = this.indexStartAddress + this.numberOfBlocks * BYTES_PER_INDEX_ENTRY;
    }

    /**
     * @return the hash code of this object.
     */
    private int calculateHashCode() {
        int result = 7;
        result = 31 * result + (int) (this.startAddress ^ this.startAddress >>> 32);
        result = 31 * result + (int) (this.subFileSize ^ this.subFileSize >>> 32);
        result = 31 * result + this.baseZoomLevel;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof SubFileParameter)) {
            return false;
        }
        final SubFileParameter other = (SubFileParameter) obj;
        return startAddress == other.startAddress && subFileSize == other.subFileSize && baseZoomLevel == other.baseZoomLevel;
    }

    public byte getBaseZoomLevel() {
        return baseZoomLevel;
    }

    public long getBlocksHeight() {
        return blocksHeight;
    }

    public long getBlocksWidth() {
        return blocksWidth;
    }

    public long getBoundaryTileBottom() {
        return boundaryTileBottom;
    }

    public long getBoundaryTileLeft() {
        return boundaryTileLeft;
    }

    public long getBoundaryTileRight() {
        return boundaryTileRight;
    }

    public long getBoundaryTileTop() {
        return boundaryTileTop;
    }

    public int getHashCodeValue() {
        return hashCodeValue;
    }

    public long getIndexEndAddress() {
        return indexEndAddress;
    }

    public long getIndexStartAddress() {
        return indexStartAddress;
    }

    public long getNumberOfBlocks() {
        return numberOfBlocks;
    }

    public long getStartAddress() {
        return startAddress;
    }

    public long getSubFileSize() {
        return subFileSize;
    }

    public byte getZoomLevelMax() {
        return zoomLevelMax;
    }

    public byte getZoomLevelMin() {
        return zoomLevelMin;
    }

    @Override
    public int hashCode() {
        return this.hashCodeValue;
    }
}
