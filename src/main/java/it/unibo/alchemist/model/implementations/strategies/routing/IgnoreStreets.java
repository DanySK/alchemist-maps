package it.unibo.alchemist.model.implementations.strategies.routing;

import it.unibo.alchemist.model.implementations.PointToPointRoute;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.model.interfaces.strategies.RoutingStrategy;

/**
 * This strategy ignores any information about the map, and connects the
 * starting and ending point with a straight line using
 * {@link PointToPointRoute}.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
public class IgnoreStreets<T> implements RoutingStrategy<T> {

	private static final long serialVersionUID = 2678088737744440021L;

	@Override
	public IRoute computeRoute(final IPosition currentPos, final IPosition finalPos) {
		return new PointToPointRoute(currentPos, finalPos);
	}

}
