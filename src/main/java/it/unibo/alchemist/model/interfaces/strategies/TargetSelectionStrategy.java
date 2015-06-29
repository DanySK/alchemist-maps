/**
 * 
 */
package it.unibo.alchemist.model.interfaces.strategies;

import java.io.Serializable;

import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;

/**
 * This interface models a strategy for selecting positions where to move.
 * 
 * @author Danilo Pianini
 *
 * @param <T>
 */
@FunctionalInterface
public interface TargetSelectionStrategy<T> extends Serializable {
	
	/**
	 * @return the next target where the {@link INode} is directed
	 */
	IPosition getNextTarget();

}
