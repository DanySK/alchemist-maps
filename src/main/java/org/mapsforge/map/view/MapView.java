/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.view;

import org.mapsforge.map.layer.LayerManager;

public interface MapView {
    /**
     * @return the FrameBuffer used in this MapView.
     */
    FrameBuffer getFrameBuffer();

    LayerManager getLayerManager();

    /**
     * Requests a redrawing as soon as possible.
     */
    void repaint();
}
