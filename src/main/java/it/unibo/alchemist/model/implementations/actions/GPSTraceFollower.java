/**
 * 
 */
package it.unibo.alchemist.model.implementations.actions;

import it.unibo.alchemist.model.implementations.PointToPointRoute;
import it.unibo.alchemist.model.interfaces.IAction;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.IRoute;

/**
 * This action blindly follows a the GPS trace associated with the current node.
 * No interaction with other nodes supported.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
public class GPSTraceFollower<T> extends AbstractMoveOnMap<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2527074124730740117L;

	/**
	 * @param environment the environment
	 * @param node the node
	 * @param reaction the reaction
	 * @param speed the speed
	 */
	public GPSTraceFollower(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction, final double speed) {
		super(environment, node, reaction, speed, 0, 0);
	}

	@Override
	public IAction<T> cloneOnNewNode(final INode<T> n, final IReaction<T> r) {
		return null;
	}

	@Override
	protected final IPosition getNextTarget() {
		return getEnvironment().getNextPosition(getNode(), getReaction().getTau());
	}
	

	@Override
	protected final boolean isInteractingNode(final INode<T> n) {
		return false;
	}

	@Override
	protected final IRoute computeRoute(final IPosition currentPos, final IPosition finalPos) {
		return new PointToPointRoute(currentPos, finalPos);
	}

}
