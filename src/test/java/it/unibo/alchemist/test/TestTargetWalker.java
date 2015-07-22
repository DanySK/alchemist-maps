package it.unibo.alchemist.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.unibo.alchemist.model.implementations.actions.TargetWalker;
import it.unibo.alchemist.model.implementations.environments.OSMEnvironment;
import it.unibo.alchemist.model.implementations.linkingrules.NoLinks;
import it.unibo.alchemist.model.implementations.molecules.Molecule;
import it.unibo.alchemist.model.implementations.nodes.GenericNode;
import it.unibo.alchemist.model.implementations.positions.LatLongPosition;
import it.unibo.alchemist.model.implementations.probabilitydistributions.DiracComb;
import it.unibo.alchemist.model.implementations.reactions.Event;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.IMolecule;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.utils.L;

import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;


/**
 * @author Danilo Pianini
 *
 */
public class TestTargetWalker {
	
	private static final String TESTMAP = "/maps/cesena.pbf";
	private static final IMolecule TRACK = new Molecule("track");
	private static final IMolecule INTERACTING = new Molecule("interacting");
	private static final int STEPS = 2000;
	private static final double STARTLAT = 44.13581;
	private static final double STARTLON = 12.2403;
	private static final double ENDLAT = 44.143493;
	private static final double ENDLON = 12.260879;
	/*
	 * Rocca Malatestiana
	 */
	private static final IPosition STARTPOSITION = new LatLongPosition(STARTLAT, STARTLON);
	/*
	 * Near Montefiore
	 */
	private static final IPosition ENDPOSITION = new LatLongPosition(ENDLAT, ENDLON);
	private IMapEnvironment<Object> env;
	private INode<Object> node;
	private IReaction<Object> reaction;
	
	/**
	 * @throws ClassNotFoundException if test fails
	 * @throws IOException if test fails
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws ClassNotFoundException, IOException {
		try {
			L.setLoggingLevel(Level.ALL);
			env = new OSMEnvironment<>(TESTMAP, true, true);
			env.setLinkingRule(new NoLinks<>());
			node = new GenericNode<Object>(true) {
				private static final long serialVersionUID = -3982001064673078159L;
				@Override
				protected Object createT() {
					return null;
				}
			};
			reaction = new Event<Object>(node, new DiracComb<>(1));
			reaction.setActions(Lists.newArrayList(new TargetWalker<Object>(env, node, reaction, TRACK, INTERACTING)));
			node.addReaction(reaction);
			env.addNode(node, STARTPOSITION);
		} catch (IllegalStateException e) {
			L.error(e);
			fail(e.getMessage());
		}
	}

	private void run() {
		IntStream.range(0, STEPS).forEach(i -> {
			reaction.execute();
			reaction.update(reaction.getTau(), true, env);
		});
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoPosition() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		run();
		/*
		 * Node should not move at all
		 */
		assertEquals(start, env.getPosition(node));
	}
	
	/**
	 * 
	 */
	@Test
	public void testIPosition() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, new LatLongPosition(ENDLAT, ENDLON));
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testIterableDouble() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, Lists.newArrayList(ENDLAT, ENDLON));
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testIterableStrings() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, Lists.newArrayList(Double.toString(ENDLAT), Double.toString(ENDLON)));
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testStrings01() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, Lists.newArrayList(ENDLAT, ENDLON).toString());
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testStrings02() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, ENDPOSITION.toString());
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testStrings03() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, "<" + ENDLAT + " " + ENDLON + ">");
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}

	/**
	 * 
	 */
	@Test
	public void testStrings04() {
		final IPosition start = env.getPosition(node);
		/*
		 * Should not be more than 10 meters afar the suggested start
		 */
		assertTrue(STARTPOSITION.getDistanceTo(start) < 10);
		node.setConcentration(TRACK, "sakldaskld" + ENDLAT + "fmekfjr" + ENDLON + "sdsad32d");
		run();
		/*
		 * Node should get to the final position
		 */
		assertEquals(ENDPOSITION, env.getPosition(node));
	}


	
}
