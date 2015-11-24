/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.graphics;

/**
 * 
 */
public interface Matrix {

    /**
     * 
     */
    void reset();

    /**
     * @param theta
     *            an angle measured in radians.
     */
    void rotate(float theta);

    /**
     * @param theta
     *            an angle measured in radians.
     * @param pivotX
     *            pivotX
     * @param pivotY
     *            pivotY
     */
    void rotate(float theta, float pivotX, float pivotY);

    /**
     * @param scaleX
     *            scaleX
     * @param scaleY
     *            scaleY
     */
    void scale(float scaleX, float scaleY);

    /**
     * @param translateX
     *            translateX
     * @param translateY
     *            translateY
     */
    void translate(float translateX, float translateY);
}
