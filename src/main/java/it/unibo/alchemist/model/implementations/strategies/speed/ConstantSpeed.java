package it.unibo.alchemist.model.implementations.strategies.speed;

import it.unibo.alchemist.model.interfaces.IAction;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.TimeDistribution;
import it.unibo.alchemist.model.interfaces.strategies.SpeedSelectionStrategy;

/**
 * This strategy makes the node move at an average constant speed, which is
 * influenced by the {@link TimeDistribution} of the {@link IReaction} hosting
 * this {@link IAction}. This action tries to normalize on the {@link IReaction}
 * rate, but if the {@link TimeDistribution} has a high variance, the movements
 * on the map will inherit this tract.
 * 
 * @param <T>
 */
public class ConstantSpeed<T> implements SpeedSelectionStrategy<T> {

    private static final long serialVersionUID = 1746429998480123049L;
    private final double sp;

    /**
     * @param reaction
     *            the reaction
     * @param speed
     *            the speed, in meters/second
     */
    public ConstantSpeed(final IReaction<T> reaction, final double speed) {
        assert speed > 0 : "Speed must be positive.";
        sp = speed / reaction.getRate();
    }

    @Override
    public double getCurrentSpeed(final IPosition target) {
        return sp;
    }

}
