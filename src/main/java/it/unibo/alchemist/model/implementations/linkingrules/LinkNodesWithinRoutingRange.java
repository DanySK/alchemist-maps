/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.implementations.linkingrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.alchemist.model.implementations.neighborhoods.Neighborhood;
import it.unibo.alchemist.model.interfaces.IEnvironment;
import it.unibo.alchemist.model.interfaces.ILinkingRule;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INeighborhood;
import it.unibo.alchemist.model.interfaces.INode;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public class LinkNodesWithinRoutingRange<T> implements ILinkingRule<T> {

	private static final long serialVersionUID = 726751817489962367L;
	private final Collection<INode<T>> emptyList = Collections.unmodifiableCollection(new ArrayList<INode<T>>(0));
	private final double range;

	/**
	 * @param r range
	 */
	public LinkNodesWithinRoutingRange(final double r) {
		range = r;
	}

	@Override
	public INeighborhood<T> computeNeighborhood(final INode<T> center, final IEnvironment<T> env) {
		if (env instanceof IMapEnvironment<?>) {
			final IMapEnvironment<T> menv = (IMapEnvironment<T>) env;
			final Stream<INode<T>> stream = menv.getNodesWithinRange(center, range).parallelStream();
			final Collection<INode<T>> filtered = stream.filter(node -> menv.computeRoute(center, node).getDistance() < range).collect(Collectors.toList());
			return new Neighborhood<>(center, filtered, menv);
		}
		return new Neighborhood<>(center, emptyList, env);
	}

}
