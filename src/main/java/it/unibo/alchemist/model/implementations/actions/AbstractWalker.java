package it.unibo.alchemist.model.implementations.actions;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.model.interfaces.Vehicle;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public abstract class AbstractWalker<T> extends AbstractMoveOnMap<T> {

	private static final long serialVersionUID = -2291955689914046763L;

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param speed
	 *            the speed at which this {@link AbstractMoveOnMap} will move
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractMoveOnMap}
	 */
	public AbstractWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double speed, final double interaction, final double range) {
		super(environment, node, reaction, speed, interaction, range);
	}
	
	@Override
	protected IRoute computeRoute(final IPosition currentPos, final IPosition finalPos) {
		return getEnvironment().computeRoute(currentPos, finalPos, Vehicle.FOOT);
	}

}
