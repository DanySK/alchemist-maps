package it.unibo.alchemist.model.implementations.actions;

import it.unibo.alchemist.model.implementations.strategies.routing.IgnoreStreets;
import it.unibo.alchemist.model.implementations.strategies.speed.ConstantSpeed;
import it.unibo.alchemist.model.implementations.strategies.speed.StraightLineTraceDependantSpeed;
import it.unibo.alchemist.model.implementations.strategies.target.FollowRoute;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IReaction;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public class ReproduceGPSTrace<T> extends MoveOnMap<T> {

	private static final long serialVersionUID = -2291955689914046763L;

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 */
	public ReproduceGPSTrace(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction) {
		super(environment, node,
				new IgnoreStreets<>(),
				new StraightLineTraceDependantSpeed<>(environment, node, reaction),
				new FollowRoute<>(environment, node, reaction));
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
	 *            the average speed
	 */
	public ReproduceGPSTrace(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double speed) {
		super(environment, node,
				new IgnoreStreets<>(),
				new ConstantSpeed<>(reaction, speed),
				new FollowRoute<>(environment, node, reaction));
	}
	
}
