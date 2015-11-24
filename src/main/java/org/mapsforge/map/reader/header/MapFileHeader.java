/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader.header;

import java.io.IOException;

import org.mapsforge.map.reader.ReadBuffer;

/**
 * Reads and validates the header data from a binary map file.
 */
public class MapFileHeader {
    /**
     * Maximum valid base zoom level of a sub-file.
     */
    private static final int BASE_ZOOM_LEVEL_MAX = 20;

    /**
     * Minimum size of the file header in bytes.
     */
    private static final int HEADER_SIZE_MIN = 70;

    /**
     * Length of the debug signature at the beginning of the index.
     */
    private static final byte SIGNATURE_LENGTH_INDEX = 16;

    /**
     * A single whitespace character.
     */
    private static final char SPACE = ' ';

    private MapFileInfo mapFileInfo;
    private SubFileParameter[] subFileParameters;
    private byte zoomLevelMaximum;
    private byte zoomLevelMinimum;

    /**
     * @return a MapFileInfo containing the header data.
     */
    public MapFileInfo getMapFileInfo() {
        return this.mapFileInfo;
    }

    /**
     * @param zoomLevel
     *            the originally requested zoom level.
     * @return the closest possible zoom level which is covered by a sub-file.
     */
    public byte getQueryZoomLevel(final byte zoomLevel) {
        if (zoomLevel > this.zoomLevelMaximum) {
            return this.zoomLevelMaximum;
        } else if (zoomLevel < this.zoomLevelMinimum) {
            return this.zoomLevelMinimum;
        }
        return zoomLevel;
    }

    /**
     * @param queryZoomLevel
     *            the zoom level for which the sub-file parameters are needed.
     * @return the sub-file parameters for the given zoom level.
     */
    public SubFileParameter getSubFileParameter(final int queryZoomLevel) {
        return this.subFileParameters[queryZoomLevel];
    }

    /**
     * Reads and validates the header block from the map file.
     * 
     * @param readBuffer
     *            the ReadBuffer for the file data.
     * @param fileSize
     *            the size of the map file in bytes.
     * @return a FileOpenResult containing an error message in case of a
     *         failure.
     * @throws IOException
     *             if an error occurs while reading the file.
     */
    public FileOpenResult readHeader(final ReadBuffer readBuffer, final long fileSize) throws IOException {
        FileOpenResult fileOpenResult = RequiredFields.readMagicByte(readBuffer);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readRemainingHeader(readBuffer);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        final MapFileInfoBuilder mapFileInfoBuilder = new MapFileInfoBuilder();

        fileOpenResult = RequiredFields.readFileVersion(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readFileSize(readBuffer, fileSize, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readMapDate(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readBoundingBox(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readTilePixelSize(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readProjectionName(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = OptionalFields.readOptionalFields(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readPoiTags(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = RequiredFields.readWayTags(readBuffer, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        fileOpenResult = readSubFileParameters(readBuffer, fileSize, mapFileInfoBuilder);
        if (!fileOpenResult.isSuccess()) {
            return fileOpenResult;
        }

        this.mapFileInfo = mapFileInfoBuilder.build();
        return FileOpenResult.SUCCESS;
    }

    private FileOpenResult readSubFileParameters(final ReadBuffer readBuffer, final long fileSize, final MapFileInfoBuilder mapFileInfoBuilder) {
        // get and check the number of sub-files (1 byte)
        final byte numberOfSubFiles = readBuffer.readByte();
        if (numberOfSubFiles < 1) {
            return new FileOpenResult("invalid number of sub-files: " + numberOfSubFiles);
        }
        mapFileInfoBuilder.setNumberOfSubFiles(numberOfSubFiles);

        final SubFileParameter[] tempSubFileParameters = new SubFileParameter[numberOfSubFiles];
        this.zoomLevelMinimum = Byte.MAX_VALUE;
        this.zoomLevelMaximum = Byte.MIN_VALUE;

        // get and check the information for each sub-file
        for (byte currentSubFile = 0; currentSubFile < numberOfSubFiles; ++currentSubFile) {
            final SubFileParameterBuilder subFileParameterBuilder = new SubFileParameterBuilder();

            // get and check the base zoom level (1 byte)
            final byte baseZoomLevel = readBuffer.readByte();
            if (baseZoomLevel < 0 || baseZoomLevel > BASE_ZOOM_LEVEL_MAX) {
                return new FileOpenResult("invalid base zooom level: " + baseZoomLevel);
            }
            subFileParameterBuilder.setBaseZoomLevel(baseZoomLevel);

            // get and check the minimum zoom level (1 byte)
            final byte zoomLevelMin = readBuffer.readByte();
            if (zoomLevelMin < 0 || zoomLevelMin > 22) {
                return new FileOpenResult("invalid minimum zoom level: " + zoomLevelMin);
            }
            subFileParameterBuilder.setZoomLevelMin(zoomLevelMin);

            // get and check the maximum zoom level (1 byte)
            final byte zoomLevelMax = readBuffer.readByte();
            if (zoomLevelMax < 0 || zoomLevelMax > 22) {
                return new FileOpenResult("invalid maximum zoom level: " + zoomLevelMax);
            }
            subFileParameterBuilder.setZoomLevelMax(zoomLevelMax);

            // check for valid zoom level range
            if (zoomLevelMin > zoomLevelMax) {
                return new FileOpenResult("invalid zoom level range: " + zoomLevelMin + SPACE + zoomLevelMax);
            }

            // get and check the start address of the sub-file (8 bytes)
            final long startAddress = readBuffer.readLong();
            if (startAddress < HEADER_SIZE_MIN || startAddress >= fileSize) {
                return new FileOpenResult("invalid start address: " + startAddress);
            }
            subFileParameterBuilder.setStartAddress(startAddress);

            long indexStartAddress = startAddress;
            if (mapFileInfoBuilder.getOptionalFields().isDebugFile()) {
                // the sub-file has an index signature before the index
                indexStartAddress += SIGNATURE_LENGTH_INDEX;
            }
            subFileParameterBuilder.setIndexStartAddress(indexStartAddress);

            // get and check the size of the sub-file (8 bytes)
            final long subFileSize = readBuffer.readLong();
            if (subFileSize < 1) {
                return new FileOpenResult("invalid sub-file size: " + subFileSize);
            }
            subFileParameterBuilder.setSubFileSize(subFileSize);

            subFileParameterBuilder.setBoundingBox(mapFileInfoBuilder.getBoundingBox());

            // add the current sub-file to the list of sub-files
            tempSubFileParameters[currentSubFile] = subFileParameterBuilder.build();

            updateZoomLevelInformation(tempSubFileParameters[currentSubFile]);
        }

        // create and fill the lookup table for the sub-files
        this.subFileParameters = new SubFileParameter[this.zoomLevelMaximum + 1];
        for (int currentMapFile = 0; currentMapFile < numberOfSubFiles; ++currentMapFile) {
            final SubFileParameter subFileParameter = tempSubFileParameters[currentMapFile];
            for (byte zoomLevel = subFileParameter.getZoomLevelMin(); zoomLevel <= subFileParameter.getZoomLevelMax(); ++zoomLevel) {
                this.subFileParameters[zoomLevel] = subFileParameter;
            }
        }
        return FileOpenResult.SUCCESS;
    }

    private void updateZoomLevelInformation(final SubFileParameter subFileParameter) {
        // update the global minimum and maximum zoom level information
        if (this.zoomLevelMinimum > subFileParameter.getZoomLevelMin()) {
            this.zoomLevelMinimum = subFileParameter.getZoomLevelMin();
        }
        if (this.zoomLevelMaximum < subFileParameter.getZoomLevelMax()) {
            this.zoomLevelMaximum = subFileParameter.getZoomLevelMax();
        }
    }
}
