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

public class NegativeRule extends Rule {
	private final AttributeMatcher attributeMatcher;

	public NegativeRule(final RuleBuilder ruleBuilder, final AttributeMatcher attributeMatcher) {
		super(ruleBuilder);

		this.attributeMatcher = attributeMatcher;
	}

	@Override
	public boolean matchesNode(final List<Tag> tags, final byte zoomLevel) {
		return this.getZoomMin() <= zoomLevel && this.getZoomMax() >= zoomLevel && this.getElementMatcher().matches(Element.NODE) && this.attributeMatcher.matches(tags);
	}

	@Override
	public boolean matchesWay(final List<Tag> tags, final byte zoomLevel, final Closed closed) {
		return this.getZoomMin() <= zoomLevel && this.getZoomMax() >= zoomLevel && this.getElementMatcher().matches(Element.WAY) && this.getClosedMatcher().matches(closed) && this.attributeMatcher.matches(tags);
	}
}
