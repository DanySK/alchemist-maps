package it.unibo.alchemist.model.implementations.strategies.routing;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.model.interfaces.Vehicle;
import it.unibo.alchemist.model.interfaces.strategies.RoutingStrategy;

/**
 * This strategy computes a route along streets allowed for a selected
 * {@link Vehicle} connecting the starting and ending point.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
public class OnStreets<T> implements RoutingStrategy<T> {
	
	private static final long serialVersionUID = 9041363003794088201L;
	private final IMapEnvironment<T> env;
	private final Vehicle vehicle;
	
	/**
	 * @param environment
	 *            the environment
	 * @param v
	 *            the {@link Vehicle}
	 */
	public OnStreets(final IMapEnvironment<T> environment, final Vehicle v) {
		env = environment;
		vehicle = v;
	}

	@Override
	public IRoute computeRoute(final IPosition currentPos, final IPosition finalPos) {
		return env.computeRoute(currentPos, finalPos, vehicle);
	}

}
