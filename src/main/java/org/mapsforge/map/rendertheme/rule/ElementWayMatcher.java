/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

public final class ElementWayMatcher implements ElementMatcher {
    private static final ElementWayMatcher INSTANCE = new ElementWayMatcher();

    public static ElementWayMatcher getInstance() {
        return INSTANCE;
    }

    private ElementWayMatcher() {
        // do nothing
    }

    @Override
    public boolean isCoveredBy(final ElementMatcher elementMatcher) {
        return elementMatcher.matches(Element.WAY);
    }

    @Override
    public boolean matches(final Element element) {
        return element == Element.WAY;
    }
}
