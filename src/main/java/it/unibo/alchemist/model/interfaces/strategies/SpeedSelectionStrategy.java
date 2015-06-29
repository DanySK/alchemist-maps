/**
 * 
 */
package it.unibo.alchemist.model.interfaces.strategies;

import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;

import java.io.Serializable;

/**
 * Given the current target {@link IPosition}, this strategy interface computes
 * the current {@link INode}'s speed.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
@FunctionalInterface
public interface SpeedSelectionStrategy<T> extends Serializable {

	/**
	 * @param target
	 *            the {@link IPosition} describing where the {@link INode} is
	 *            directed
	 * @return the current node's speed
	 */
	double getCurrentSpeed(IPosition target);

}
