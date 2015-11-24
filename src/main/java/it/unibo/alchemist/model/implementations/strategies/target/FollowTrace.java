package it.unibo.alchemist.model.implementations.strategies.target;

import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.model.interfaces.strategies.TargetSelectionStrategy;

/**
 * This strategy follows a {@link IRoute}.
 * 
 * @param <T>
 */
public class FollowTrace<T> implements TargetSelectionStrategy<T> {

    private static final long serialVersionUID = -446053307821810437L;
    private final IMapEnvironment<T> environment;
    private final INode<T> node;
    private final IReaction<T> reaction;

    /**
     * @param env
     *            the environment
     * @param n
     *            the node
     * @param r
     *            the reaction
     */
    public FollowTrace(final IMapEnvironment<T> env, final INode<T> n, final IReaction<T> r) {
        environment = env;
        node = n;
        reaction = r;
    }

    @Override
    public final IPosition getNextTarget() {
        return environment.getNextPosition(node, reaction.getTau());
    }

}
