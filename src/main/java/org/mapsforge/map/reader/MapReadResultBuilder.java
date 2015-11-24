/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

import java.util.ArrayList;
import java.util.List;

public class MapReadResultBuilder {
    private boolean isWater;
    private final List<PointOfInterest> pointOfInterests;
    private final List<Way> ways;

    public MapReadResultBuilder() {
        this.pointOfInterests = new ArrayList<PointOfInterest>();
        this.ways = new ArrayList<Way>();
    }

    public void add(final PoiWayBundle poiWayBundle) {
        this.pointOfInterests.addAll(poiWayBundle.getPois());
        this.ways.addAll(poiWayBundle.getWays());
    }

    public MapReadResult build() {
        return new MapReadResult(this);
    }

    public List<PointOfInterest> getPointOfInterests() {
        return pointOfInterests;
    }

    public List<Way> getWays() {
        return ways;
    }

    public boolean isWater() {
        return isWater;
    }

    public void setWater(final boolean isWater) {
        this.isWater = isWater;
    }
}
