/**
 * 
 */
package it.unibo.alchemist.model.interfaces.strategies;

import java.io.Serializable;

import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IRoute;

/**
 * Strategy interface describing how the routing between two points happens.
 * 
 * @param <T> Concentration type
 */
@FunctionalInterface
public interface RoutingStrategy<T> extends Serializable {

    /**
     * Computes a route between two positions.
     * 
     * @param currentPos starting {@link IPosition}
     * @param finalPos ending {@link IPosition}
     * @return a {@link IRoute} connecting the two points
     */
    IRoute computeRoute(IPosition currentPos, IPosition finalPos);

}
