package it.unibo.alchemist.model.implementations.strategies.speed;

import it.unibo.alchemist.model.interfaces.IGPSPoint;
import it.unibo.alchemist.model.interfaces.IGPSTrace;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.strategies.SpeedSelectionStrategy;

import org.danilopianini.lang.LangUtils;

/**
 * This strategy dynamically tries to move the node adjusting its speed to
 * synchronize the reaction rate and the traces data.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
public abstract class TraceDependantSpeed<T> implements SpeedSelectionStrategy<T> {

	private static final long serialVersionUID = 8021140539083062866L;
	private final IGPSTrace trace;
	private final IReaction<T> reaction;
	private final IMapEnvironment<T> env;
	private final INode<T> node;

	/**
	 * @param e
	 *            the environment
	 * @param n
	 *            the node
	 * @param r
	 *            the reaction
	 */
	public TraceDependantSpeed(final IMapEnvironment<T> e, final INode<T> n, final IReaction<T> r) {
		LangUtils.requireNonNull(e, n, r);
		env = e;
		node = n;
		reaction = r;
		trace = env.getTrace(node);
	}

	@Override
	public final double getCurrentSpeed(final IPosition target) {
		final double curTime = reaction.getTau().toDouble();
		final IGPSPoint next = trace.getNextPosition(curTime);
		final double expArrival = next.getTime();
		final double frequency = reaction.getRate();
		final double steps = (expArrival - curTime) * frequency;
		return computeDistance(env, node, target) / steps;
	}

	/**
	 * @param environment
	 *            the environment
	 * @param curNode
	 *            the node
	 * @param targetPosition
	 *            the target
	 * @return an estimation of the distance between the node and the target
	 *         position
	 */
	protected abstract double computeDistance(IMapEnvironment<T> environment, INode<T> curNode, IPosition targetPosition);

}
