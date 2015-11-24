/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.scalebar;

public final class Metric implements Adapter {
    private static final Metric INSTANCE = new Metric();
    private static final int ONE_KILOMETER = 1000;
    private static final int[] SCALE_BAR_VALUES = { 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 };

    public static Metric getInstance() {
        return INSTANCE;
    }

    private Metric() {
        // do nothing
    }

    @Override
    public double getMeterRatio() {
        return 1;
    }

    @Override
    public int[] getScaleBarValues() {
        return SCALE_BAR_VALUES;
    }

    @Override
    public String getScaleText(final int mapScaleValue) {
        if (mapScaleValue < ONE_KILOMETER) {
            return mapScaleValue + " m";
        }
        return mapScaleValue / ONE_KILOMETER + " km";
    }
}
