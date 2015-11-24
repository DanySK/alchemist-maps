/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model;

import org.mapsforge.core.model.Dimension;
import org.mapsforge.map.model.common.Observable;

public class MapViewModel extends Observable {
    private Dimension dimension;

    /**
     * @return the current dimension of the {@code MapView} (may be null).
     */
    public synchronized Dimension getDimension() {
        return this.dimension;
    }

    public void setDimension(final Dimension dimension) {
        synchronized (this) {
            this.dimension = dimension;
        }
        notifyObservers();
    }
}
