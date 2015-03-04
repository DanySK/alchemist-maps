/*
 * Copyright (C) 2010-2015, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.implementations.actions;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.utils.MapUtils;

import java.util.Collection;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public abstract class AbstractWalker<T> extends AbstractMoveNode<T> {

	/**
	 * Default speed in meters per second.
	 */
	public static final double DEFAULT_SPEED = 1.5;
	/**
	 * Minimum distance to walk per step in meters. Under this value, the
	 * movement will become imprecise, due to errors in computation of the
	 * distance between two points on the surface of the Earth.
	 */
	public static final double MINIMUM_DISTANCE_WALKED = 1.0;
	/**
	 * Default interaction range.
	 */
	public static final double DEFAULT_RANGE = 10;
	private static final long serialVersionUID = -2268285113653315764L;
	private IPosition end;
	private IRoute route;
	private final IReaction<T> rt;
	private int curStep;
	private final double sp, in, rd;

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 */
	public AbstractWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction) {
		this(environment, node, reaction, DEFAULT_RANGE);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param interaction
	 *            the higher, the more the {@link AbstractWalker} slows down
	 *            when obstacles are found
	 */
	public AbstractWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double interaction) {
		this(environment, node, reaction, interaction, DEFAULT_RANGE);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param interaction
	 *            the higher, the more the {@link AbstractWalker} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractWalker}
	 */
	public AbstractWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double interaction, final double range) {
		this(environment, node, reaction, DEFAULT_SPEED, interaction, range);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param speed
	 *            the speed at which this {@link AbstractWalker} will move
	 * @param interaction
	 *            the higher, the more the {@link AbstractWalker} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractWalker}
	 */
	public AbstractWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double speed, final double interaction, final double range) {
		super(environment, node, true);
		rt = reaction;
		sp = speed / reaction.getRate();
		in = interaction;
		rd = range;
	}

	/**
	 * @return the speed in this very moment, computed considering the
	 *         interacting nodes in the surroundings
	 */
	protected double getCurrentSpeed() {
		double crowd = 0;
		final IMapEnvironment<T> env = getEnvironment();
		final INode<T> node = getNode();
		final Collection<? extends INode<T>> neighs = env.getNodesWithinRange(node, rd);
		if (neighs.size() > 1 / in) {
			for (final INode<T> neigh : neighs) {
				if (isInteractingNode(neigh)) {
					crowd += 1 / env.getDistanceBetweenNodes(node, neigh);
				}
			}
		}
		return Math.max(sp / (crowd * in + 1), MINIMUM_DISTANCE_WALKED);
	}

	@Override
	public IMapEnvironment<T> getEnvironment() {
		return (IMapEnvironment<T>) super.getEnvironment();
	}

	@Override
	public IPosition getNextPosition() {
		final IPosition previousEnd = end;
		end = getNextTarget();
		if (!end.equals(previousEnd)) {
			resetRoute();
		}
		double maxWalk = getCurrentSpeed();
		final IMapEnvironment<T> env = getEnvironment();
		final INode<T> node = getNode();
		final IPosition curPos = env.getPosition(node);
		if (curPos.getDistanceTo(end) <= maxWalk) {
			final IPosition destination = end;
			end = getNextTarget();
			resetRoute();
			return destination;
		}
		if (route == null) {
			route = env.computeRoute(node, end);
		}
		if (route.getPointsNumber() < 1) {
			resetRoute();
			return MapUtils.getDestinationLocation(curPos, end, maxWalk);
		}
		IPosition target = null;
		double toWalk;
		do {
			target = route.getPoint(curStep);
			toWalk = target.getDistanceTo(curPos);
			if (toWalk > maxWalk) {
				return MapUtils.getDestinationLocation(curPos, target, maxWalk);
			}
			curStep++;
			maxWalk -= toWalk;
		} while (curStep != route.getPointsNumber());
		/*
		 * I've followed the whole route
		 */
		resetRoute();
		target = end;
		return MapUtils.getDestinationLocation(curPos, target, maxWalk);
	}

	/**
	 * this method is called when the {@link AbstractWalker} needs a new target
	 * (e.g. because it reached the previous)
	 * 
	 * @return the new target
	 */
	protected abstract IPosition getNextTarget();

	/**
	 * @return the reaction
	 */
	protected IReaction<T> getReaction() {
		return rt;
	}

	/**
	 * @return the average speed
	 */
	protected final double getSpeed() {
		return sp;
	}

	/**
	 * @return the current target
	 */
	protected final IPosition getTargetPoint() {
		return end;
	}

	/**
	 * @param n
	 *            determines wether a node is interacting
	 * @return true if the passed node is to be considered an obstacle
	 */
	protected abstract boolean isInteractingNode(final INode<T> n);

	/**
	 * Resets the current route, e.g. because the target has been reached
	 */
	protected final void resetRoute() {
		route = null;
		curStep = 0;
	}
	
	/**
	 * @param p
	 *            the new target
	 */
	protected final void setTargetPoint(final IPosition p) {
		end = p;
	}
	
	/**
	 * @return the current route, or null if no route is currently being followed
	 */
	protected final IRoute getCurrentRoute() {
		return route;
	}

}
