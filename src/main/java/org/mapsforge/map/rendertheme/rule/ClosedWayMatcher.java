/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

public final class ClosedWayMatcher implements ClosedMatcher {
	private static final ClosedWayMatcher INSTANCE = new ClosedWayMatcher();

	public static ClosedWayMatcher getInstance() {
		return INSTANCE;
	}

	private ClosedWayMatcher() {
		// do nothing
	}

	@Override
	public boolean isCoveredBy(final ClosedMatcher closedMatcher) {
		return closedMatcher.matches(Closed.YES);
	}

	@Override
	public boolean matches(final Closed closed) {
		return closed == Closed.YES;
	}
}
