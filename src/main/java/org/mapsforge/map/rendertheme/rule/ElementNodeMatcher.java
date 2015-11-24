/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

public final class ElementNodeMatcher implements ElementMatcher {
    private static final ElementNodeMatcher INSTANCE = new ElementNodeMatcher();

    public static ElementNodeMatcher getInstance() {
        return INSTANCE;
    }

    private ElementNodeMatcher() {
        // do nothing
    }

    @Override
    public boolean isCoveredBy(final ElementMatcher elementMatcher) {
        return elementMatcher.matches(Element.NODE);
    }

    @Override
    public boolean matches(final Element element) {
        return element == Element.NODE;
    }
}
