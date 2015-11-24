/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.util.List;

import org.mapsforge.core.model.Tag;

public class NegativeMatcher implements AttributeMatcher {
    private final List<String> keyList;
    private final List<String> valueList;

    public NegativeMatcher(final List<String> keyList, final List<String> valueList) {
        this.keyList = keyList;
        this.valueList = valueList;
    }

    @Override
    public boolean isCoveredBy(final AttributeMatcher attributeMatcher) {
        return false;
    }

    private boolean keyListDoesNotContainKeys(final List<Tag> tags) {
        final int n = tags.size();
        for (int i = 0; i < n; ++i) {
            if (this.keyList.contains(tags.get(i).getKey())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matches(final List<Tag> tags) {
        if (keyListDoesNotContainKeys(tags)) {
            return true;
        }

        final int n = tags.size();
        for (int i = 0; i < n; ++i) {
            if (this.valueList.contains(tags.get(i).getValue())) {
                return true;
            }
        }
        return false;
    }
}
