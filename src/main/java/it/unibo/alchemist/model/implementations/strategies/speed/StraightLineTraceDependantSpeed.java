/**
 * 
 */
package it.unibo.alchemist.model.implementations.strategies.speed;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;

/**
 * This {@link TraceDependantSpeed} uses the distance between coordinates for estimating the distance.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
public class StraightLineTraceDependantSpeed<T> extends TraceDependantSpeed<T> {

	private static final long serialVersionUID = 539968590628143027L;

	/**
	 * @param e
	 *            the environment
	 * @param n
	 *            the node
	 * @param r
	 *            the reaction
	 */
	public StraightLineTraceDependantSpeed(final IMapEnvironment<T> e, final INode<T> n, final IReaction<T> r) {
		super(e, n, r);
	}

	@Override
	protected double computeDistance(final IMapEnvironment<T> environment, final INode<T> curNode, final IPosition targetPosition) {
		return environment.getPosition(curNode).getDistanceTo(targetPosition);
	}

}
