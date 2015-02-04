/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

public final class LinearWayMatcher implements ClosedMatcher {
	private static final LinearWayMatcher INSTANCE = new LinearWayMatcher();

	public static LinearWayMatcher getInstance() {
		return INSTANCE;
	}

	private LinearWayMatcher() {
		// do nothing
	}

	@Override
	public boolean isCoveredBy(final ClosedMatcher closedMatcher) {
		return closedMatcher.matches(Closed.NO);
	}

	@Override
	public boolean matches(final Closed closed) {
		return closed == Closed.NO;
	}
}
