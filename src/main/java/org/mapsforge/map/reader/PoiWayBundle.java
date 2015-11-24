/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.reader;

import java.util.List;

public class PoiWayBundle {
    private final List<PointOfInterest> pois;
    private final List<Way> ways;

    public PoiWayBundle(final List<PointOfInterest> pois, final List<Way> ways) {
        this.pois = pois;
        this.ways = ways;
    }

    public List<PointOfInterest> getPois() {
        return pois;
    }

    public List<Way> getWays() {
        return ways;
    }
}
