/*
 * Copyright (C) 2010-2015, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.implementations.actions;

import java.util.Objects;

import it.unibo.alchemist.model.interfaces.IGPSPoint;
import it.unibo.alchemist.model.interfaces.IGPSTrace;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;

/**
 * A walker that follows a trace. The trace is mandatory.
 * 
 * @author Danilo Pianini
 * 
 * @param <T> Concentration Time
 */
public class GPSTracesWalker<T> extends AbstractWalker<T> {

	private static final long serialVersionUID = -6495138719085165782L;
	private IGPSTrace trace;
	
	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction
	 */
	public GPSTracesWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction) {
		super(environment, node, reaction, 0, 0, 0);
	}

	@Override
	public GPSTracesWalker<T> cloneOnNewNode(final INode<T> n, final IReaction<T> r) {
		return new GPSTracesWalker<>(getEnvironment(), n, r);
	}

	/**
	 * @return the next position in the trace
	 */
	protected IPosition getNextTarget() {
		return getEnvironment().getNextPosition(getNode(), getReaction().getTau());
	}
	
	@Override
	protected double getCurrentSpeed() {
		if (trace == null) {
			trace = getEnvironment().getTrace(getNode());
			Objects.requireNonNull(trace);
		}
		final double curTime = getReaction().getTau().toDouble();
		final IGPSPoint next = trace.getNextPosition(curTime);
		final double expArrival = next.getTime();
		final double distance = getEnvironment().computeRoute(getNode(), getTargetPoint()).getDistance();
		final double frequency = getReaction().getRate();
		final double steps = (expArrival - curTime) * frequency;
		return distance / steps;
	}

	
	@Override
	protected boolean isInteractingNode(final INode<T> n) {
		return false;
	}

}
