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

public final class AnyMatcher implements ElementMatcher, AttributeMatcher, ClosedMatcher {
	private static final AnyMatcher INSTANCE = new AnyMatcher();

	public static AnyMatcher getInstance() {
		return INSTANCE;
	}

	private AnyMatcher() {
		// do nothing
	}

	@Override
	public boolean isCoveredBy(final AttributeMatcher attributeMatcher) {
		return attributeMatcher == this;
	}

	@Override
	public boolean isCoveredBy(final ClosedMatcher closedMatcher) {
		return closedMatcher == this;
	}

	@Override
	public boolean isCoveredBy(final ElementMatcher elementMatcher) {
		return elementMatcher == this;
	}

	@Override
	public boolean matches(final Closed closed) {
		return true;
	}

	@Override
	public boolean matches(final Element element) {
		return true;
	}

	@Override
	public boolean matches(final List<Tag> tags) {
		return true;
	}
}
