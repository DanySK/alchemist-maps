/**
 * 
 */
package it.unibo.alchemist.model.implementations.actions;

import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;

import it.unibo.alchemist.model.implementations.molecules.Molecule;
import it.unibo.alchemist.model.implementations.positions.LatLongPosition;
import it.unibo.alchemist.model.interfaces.IAction;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.IMolecule;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import static org.danilopianini.lang.RegexUtil.FLOAT_PATTERN;

/**
 * @author Danilo Pianini
 * @param <T>
 *
 */
public class TargetWalker<T> extends AbstractWalker<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5097382908560832035L;
	private final IMolecule track;
	private final IMolecule interacting;

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param speed
	 *            the speed at which this {@link AbstractMoveOnMap} will move
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractMoveOnMap}
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final IMolecule trackMolecule, final IMolecule interactingMolecule, final double speed, final double interaction,
			final double range) {
		super(environment, node, reaction, speed, interaction, range);
		track = trackMolecule;
		interacting = interactingMolecule;
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractMoveOnMap}
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final IMolecule trackMolecule, final IMolecule interactingMolecule, final double interaction, final double range) {
		this(environment, node, reaction, trackMolecule, interactingMolecule, DEFAULT_SPEED, interaction, range);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final IMolecule trackMolecule, final IMolecule interactingMolecule, final double interaction) {
		this(environment, node, reaction, trackMolecule, interactingMolecule, interaction, DEFAULT_RANGE);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final IMolecule trackMolecule, final IMolecule interactingMolecule) {
		this(environment, node, reaction, trackMolecule, interactingMolecule, DEFAULT_INTERACTION);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param speed
	 *            the speed at which this {@link AbstractMoveOnMap} will move
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractMoveOnMap}
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final String trackMolecule, final String interactingMolecule, final double speed, final double interaction,
			final double range) {
		this(environment, node, reaction, new Molecule(trackMolecule), new Molecule(interactingMolecule), speed, interaction, range);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 * @param range
	 *            the range in which searching for possible obstacles. Obstacles
	 *            slow down the {@link AbstractMoveOnMap}
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final String trackMolecule, final String interactingMolecule, final double interaction, final double range) {
		this(environment, node, reaction, trackMolecule, interactingMolecule, DEFAULT_SPEED, interaction, range);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 * @param interaction
	 *            the higher, the more the {@link AbstractMoveOnMap} slows down
	 *            when obstacles are found
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final String trackMolecule, final String interactingMolecule, final double interaction) {
		this(environment, node, reaction, trackMolecule, interactingMolecule, interaction, DEFAULT_RANGE);
	}

	/**
	 * @param environment
	 *            the environment
	 * @param node
	 *            the node
	 * @param reaction
	 *            the reaction. Will be used to compute the distance to walk in
	 *            every step, relying on {@link IReaction}'s getRate() method.
	 * @param trackMolecule
	 *            the molecule to track. Its value will be read when it is time
	 *            to compute a new target. If it is a {@link LatLongPosition},
	 *            it will be used as-is. If it is an {@link Iterable}, the first
	 *            two values (if they are present and they are numbers, or
	 *            Strings parse-able to numbers) will be used to create a new
	 *            {@link LatLongPosition}. Otherwise, the {@link Object} bound
	 *            to this {@link IMolecule} will be converted to a String, and
	 *            the String will be parsed using the float regular expression
	 *            matcher in Javalib.
	 * @param interactingMolecule
	 *            the molecule that decides wether or not a node is physically
	 *            interacting with the node in which this action is executed,
	 *            slowing this node down. The node will be considered
	 *            "interacting" if such molecule is present, regardless its
	 *            value.
	 */
	public TargetWalker(final IMapEnvironment<T> environment, final INode<T> node, final IReaction<T> reaction,
			final String trackMolecule, final String interactingMolecule) { 
		this(environment, node, reaction, trackMolecule, interactingMolecule, DEFAULT_INTERACTION);
	}

	@Override
	public IAction<T> cloneOnNewNode(final INode<T> n, final IReaction<T> r) {
		return new TargetWalker<>(getEnvironment(), getNode(), getReaction(), track, interacting, getSpeed(),
				getInteractionCoefficient(), getInteractionRange());
	}

	@Override
	protected IPosition getNextTarget() {
		final Optional<T> optt = getConcentration(track);
		if (optt.isPresent()) {
			final T conc = optt.get();
			if (conc instanceof LatLongPosition) {
				return (LatLongPosition) conc;
			}
			double lat = Double.NaN;
			double lon = Double.NaN;
			if (conc instanceof Iterable) {
				final Iterator<?> iterator = ((Iterable<?>) conc).iterator();
				while (iterator.hasNext() && Double.isNaN(lon)) {
					final Object elem = iterator.next();
					double val;
					if (elem instanceof Number) {
						val = ((Number) elem).doubleValue();
					} else if (elem == null) {
						return getCurrentPosition();
					} else {
						try {
							val = Double.parseDouble(elem.toString());
						} catch (NumberFormatException e) {
							return getCurrentPosition();
						}
					}
					if (Double.isNaN(lat)) {
						lat = val;
					} else {
						lon = val;
					}
				}
			} else {
				final Matcher m = FLOAT_PATTERN.matcher(conc instanceof CharSequence ? (CharSequence) conc : conc.toString());
				while (Double.isNaN(lon) && m.find()) {
					final String val = m.group();
					/*
					 * It can not fail, unless the RegexUtil utility is broken
					 */
					if (Double.isNaN(lat)) {
						lat = Double.parseDouble(val);
					} else {
						lon = Double.parseDouble(val);
					}
				}
			}
			if (!Double.isNaN(lon)) {
				return new LatLongPosition(lat, lon);
			}
		}
		return getCurrentPosition();
	}
	
	@Override
	protected boolean isInteractingNode(final INode<T> n) {
		return n.contains(interacting);
	}

}
