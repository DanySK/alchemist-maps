/**
 * 
 */
package it.unibo.alchemist.model.implementations.strategies.speed;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.Vehicle;

/**
 * This {@link TraceDependantSpeed} strategy computes the remaining distance by
 * relying on maps data for a selected {@link Vehicle}.
 * 
 * @param <T>
 */
public class RoutingTraceDependantSpeed<T> extends TraceDependantSpeed<T> {

    private static final long serialVersionUID = -2195494825891818353L;
    private final Vehicle v;

    /**
     * @param e
     *            the environment
     * @param n
     *            the node
     * @param r
     *            the reaction
     * @param vehicle
     *            the vehicle
     */
    public RoutingTraceDependantSpeed(final IMapEnvironment<T> e, final INode<T> n, final IReaction<T> r, final Vehicle vehicle) {
        super(e, n, r);
        v = vehicle;
    }

    @Override
    protected double computeDistance(final IMapEnvironment<T> environment, final INode<T> curNode, final IPosition targetPosition) {
        return environment.computeRoute(curNode, targetPosition, v).getDistance();
    }

}
