/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.util.Stack;
import java.util.logging.Logger;

public final class RuleOptimizer {
	private static final Logger LOGGER = Logger.getLogger(RuleOptimizer.class.getName());

	public static AttributeMatcher optimize(final AttributeMatcher attributeMatcher, final Stack<Rule> ruleStack) {
		if (attributeMatcher instanceof AnyMatcher || attributeMatcher instanceof NegativeMatcher) {
			return attributeMatcher;
		} else if (attributeMatcher instanceof KeyMatcher) {
			return optimizeKeyMatcher(attributeMatcher, ruleStack);
		} else if (attributeMatcher instanceof ValueMatcher) {
			return optimizeValueMatcher(attributeMatcher, ruleStack);
		}

		throw new IllegalArgumentException("unknown AttributeMatcher: " + attributeMatcher);
	}

	public static ClosedMatcher optimize(final ClosedMatcher closedMatcher, final Stack<Rule> ruleStack) {
		if (closedMatcher instanceof AnyMatcher) {
			return closedMatcher;
		}

		final int n = ruleStack.size();
		for (int i = 0; i < n; ++i) {
			if (ruleStack.get(i).getClosedMatcher().isCoveredBy(closedMatcher)) {
				return AnyMatcher.getInstance();
			} else if (!closedMatcher.isCoveredBy(ruleStack.get(i).getClosedMatcher())) {
				LOGGER.warning("unreachable rule (closed)");
			}
		}

		return closedMatcher;
	}

	public static ElementMatcher optimize(final ElementMatcher elementMatcher, final Stack<Rule> ruleStack) {
		if (elementMatcher instanceof AnyMatcher) {
			return elementMatcher;
		}

		final int n = ruleStack.size();
		for (int i = 0; i < n; ++i) {
			final Rule rule = ruleStack.get(i);
			if (rule.getElementMatcher().isCoveredBy(elementMatcher)) {
				return AnyMatcher.getInstance();
			} else if (!elementMatcher.isCoveredBy(rule.getElementMatcher())) {
				LOGGER.warning("unreachable rule (e)");
			}
		}

		return elementMatcher;
	}

	private static AttributeMatcher optimizeKeyMatcher(final AttributeMatcher attributeMatcher, final Stack<Rule> ruleStack) {
		final int n = ruleStack.size();
		for (int i = 0; i < n; ++i) {
			if (ruleStack.get(i) instanceof PositiveRule) {
				final PositiveRule positiveRule = (PositiveRule) ruleStack.get(i);
				if (positiveRule.getKeyMatcher().isCoveredBy(attributeMatcher)) {
					return AnyMatcher.getInstance();
				}
			}
		}

		return attributeMatcher;
	}

	private static AttributeMatcher optimizeValueMatcher(final AttributeMatcher attributeMatcher, final Stack<Rule> ruleStack) {
		final int n = ruleStack.size();
		for (int i = 0; i < n; ++i) {
			if (ruleStack.get(i) instanceof PositiveRule) {
				final PositiveRule positiveRule = (PositiveRule) ruleStack.get(i);
				if (positiveRule.getValueMatcher().isCoveredBy(attributeMatcher)) {
					return AnyMatcher.getInstance();
				}
			}
		}

		return attributeMatcher;
	}

	private RuleOptimizer() {
		throw new IllegalStateException();
	}
}
