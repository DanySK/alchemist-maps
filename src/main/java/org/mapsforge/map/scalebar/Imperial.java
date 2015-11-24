/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.scalebar;

public final class Imperial implements Adapter {
    private static final Imperial INSTANCE = new Imperial();
    private static final double METER_FOOT_RATIO = 0.3048;
    private static final int ONE_MILE = 5280;
    private static final int[] SCALE_BAR_VALUES = { 26400000, 10560000, 5280000, 2640000, 1056000, 528000, 264000, 105600, 52800, 26400, 10560, 5280, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 };

    public static Imperial getInstance() {
        return INSTANCE;
    }

    private Imperial() {
        // do nothing
    }

    @Override
    public double getMeterRatio() {
        return METER_FOOT_RATIO;
    }

    @Override
    public int[] getScaleBarValues() {
        return SCALE_BAR_VALUES;
    }

    @Override
    public String getScaleText(final int mapScaleValue) {
        if (mapScaleValue < ONE_MILE) {
            return mapScaleValue + " ft";
        }
        return mapScaleValue / ONE_MILE + " mi";
    }
}
