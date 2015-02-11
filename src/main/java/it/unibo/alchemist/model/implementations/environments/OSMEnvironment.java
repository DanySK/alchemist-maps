/*
 * Copyright (C) 2010-2015, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.implementations.environments;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import it.unibo.alchemist.Global;
import it.unibo.alchemist.model.implementations.GraphHopperRoute;
import it.unibo.alchemist.model.implementations.positions.LatLongPosition;
import it.unibo.alchemist.model.interfaces.IGPSTrace;
import it.unibo.alchemist.model.interfaces.IMapEnvironment;
import it.unibo.alchemist.model.interfaces.INode;
import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IRoute;
import it.unibo.alchemist.model.interfaces.ITime;
import it.unibo.alchemist.model.interfaces.Vehicle;
import it.unibo.alchemist.utils.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.danilopianini.concurrency.FastReadWriteLock;
import org.danilopianini.io.FileUtilities;
import org.danilopianini.lang.Couple;
import org.danilopianini.lang.MaxSizeHashMap;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.shapes.GHPoint;

/**
 * This class serves as template for more specific implementations of
 * environments using a map. It encloses the navigation logic, but leaves the
 * subclasses to decide how to provide map data (e.g. loading from disk or rely
 * on online services). The data is then stored in-memory for performance
 * reasons.
 * 
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public class OSMEnvironment<T> extends Continuous2DEnvironment<T> implements IMapEnvironment<T> {

	/**
	 * Default maximum communication range (in meters).
	 */
	public static final double DEFAULT_MAX_RANGE = 100;

	/**
	 * Default value for id loading from traces.
	 */
	public static final boolean DEFAULT_USE_TRACES_ID = false;

	/**
	 * The default routing algorithm.
	 */
	public static final String DEFAULT_ALGORITHM = "dijkstrabi";

	/**
	 * The default routing strategy.
	 */
	public static final String ROUTING_STRATEGY = "fastest";

	/**
	 * The default value for the force nodes on streets option.
	 */
	public static final boolean DEFAULT_ON_STREETS = true;

	/**
	 * The default value for the discard of nodes too far from streets option.
	 */
	public static final boolean DEFAULT_FORCE_STREETS = true;
	
	private static final int ROUTES_CACHE_SIZE = 100000;

	private static final String MONITOR = "MapDisplay";
	private static final long serialVersionUID = -8100726226966471621L;

	private final String mapFile;
	private String dir;
	private final TIntObjectMap<IGPSTrace> traces = new TIntObjectHashMap<>();
	private final boolean forceStreets, onlyStreet;
	private final transient FastReadWriteLock mapLock = new FastReadWriteLock();
	private final transient Map<Vehicle, GraphHopper> navigators = new EnumMap<>(Vehicle.class);
	private final transient Map<Couple<IPosition>, IRoute> routecache = new MaxSizeHashMap<>(ROUTES_CACHE_SIZE);

	/**
	 * @param file
	 *            the file path where the map data is stored
	 * @throws IOException
	 *             if the map file is not found, or it's not readable, or
	 *             accessible, or a file system error occurred, or you kicked
	 *             your hard drive while Alchemist was reading the map
	 * @throws ClassNotFoundException
	 *             if there is a gigantic bug in the distribution and
	 *             {@link IGPSTrace} or {@link List} cannot be loaded
	 */
	public OSMEnvironment(final String file) throws IOException, ClassNotFoundException {
		this(file, null, 0);
	}

	/**
	 * @param file
	 *            the file path where the map data is stored
	 * @param onStreets
	 *            if true, the nodes will be placed on the street nearest to the
	 *            desired {@link IPosition}. This setting is automatically
	 *            overridden if GPS traces are used, and a matching trace id is
	 *            available for the node.
	 * @param onlyOnStreets
	 *            if true, the nodes which are too far from a street will be
	 *            simply discarded. If false, they will be placed anyway, in the
	 *            original position.
	 * @throws IOException
	 *             if the map file is not found, or it's not readable, or
	 *             accessible, or a file system error occurred, or you kicked
	 *             your hard drive while Alchemist was reading the map
	 * @throws ClassNotFoundException
	 *             if there is a gigantic bug in the distribution and
	 *             {@link IGPSTrace} or {@link List} cannot be loaded
	 */
	public OSMEnvironment(final String file, final boolean onStreets, final boolean onlyOnStreets) throws IOException, ClassNotFoundException {
		this(file, null, 0, onStreets, onlyOnStreets, DEFAULT_USE_TRACES_ID);
	}

	/**
	 * @param file
	 *            the file path where the map data is stored. Accepts OSM maps
	 *            of any format (xml, osm, pbf). The map will be processed,
	 *            optimized and stored for future use.
	 * @param tfile
	 *            the file path where the traces are stored. Supports only
	 *            Alchemist's AGT traces. Can be null.
	 * @param ttime
	 *            the minimum time to consider when using the trace
	 * @throws IOException
	 *             if the map file is not found, or it's not readable, or
	 *             accessible, or a file system error occurred, or you kicked
	 *             your hard drive while Alchemist was reading the map
	 * @throws ClassNotFoundException
	 *             if there is a gigantic bug in the distribution and
	 *             {@link IGPSTrace} or {@link List} cannot be loaded
	 */
	public OSMEnvironment(final String file, final String tfile, final double ttime) throws IOException, ClassNotFoundException {
		this(file, tfile, ttime, DEFAULT_USE_TRACES_ID);
	}

	/**
	 * @param file
	 *            the file path where the map data is stored. Accepts OSM maps
	 *            of any format (xml, osm, pbf). The map will be processed,
	 *            optimized and stored for future use.
	 * @param tfile
	 *            the file path where the traces are stored. Supports only
	 *            Alchemist's AGT traces. Can be null.
	 * @param ttime
	 *            the minimum time to consider when using the trace
	 * @param useIds
	 *            true if you want the association node - trace to be made with
	 *            respect to the ids stored in the traces. Otherwise, ids are
	 *            generated starting from 0.
	 * @throws IOException
	 *             if the map file is not found, or it's not readable, or
	 *             accessible, or a file system error occurred, or you kicked
	 *             your hard drive while Alchemist was reading the map
	 * @throws ClassNotFoundException
	 *             if there is a gigantic bug in the distribution and
	 *             {@link IGPSTrace} or {@link List} cannot be loaded
	 */
	public OSMEnvironment(final String file, final String tfile, final double ttime, final boolean useIds) throws IOException, ClassNotFoundException {
		this(file, tfile, ttime, DEFAULT_ON_STREETS, DEFAULT_FORCE_STREETS, useIds);
	}

	/**
	 * @param file
	 *            the file path where the map data is stored. Accepts OSM maps
	 *            of any format (xml, osm, pbf). The map will be processed,
	 *            optimized and stored for future use.
	 * @param tfile
	 *            the file path where the traces are stored. Supports only
	 *            Alchemist's AGT traces. Can be null.
	 * @param ttime
	 *            the minimum time to consider when using the trace
	 * @param onStreets
	 *            if true, the nodes will be placed on the street nearest to the
	 *            desired {@link IPosition}. This setting is automatically
	 *            overridden if GPS traces are used, and a matching trace id is
	 *            available for the node.
	 * @param onlyOnStreets
	 *            if true, the nodes which are too far from a street will be
	 *            simply discarded. If false, they will be placed anyway, in the
	 *            original position.
	 * @param useIds
	 *            true if you want the association node - trace to be made with
	 *            respect to the ids stored in the traces. Otherwise, ids are
	 *            generated starting from 0.
	 * @throws IOException
	 *             if the map file is not found, or it's not readable, or
	 *             accessible, or a file system error occurred, or you kicked
	 *             your hard drive while Alchemist was reading the map
	 * @throws ClassNotFoundException
	 *             if there is a gigantic bug in the distribution and
	 *             {@link IGPSTrace} or {@link List} cannot be loaded
	 */
	@SuppressWarnings("unchecked")
	protected OSMEnvironment(final String file, final String tfile, final double ttime, final boolean onStreets, final boolean onlyOnStreets, final boolean useIds) throws IOException, ClassNotFoundException {
		super();
		final File mapfile = new File(file);
		if (!mapfile.exists()) {
			throw new FileNotFoundException(file);
		}
		List<IGPSTrace> trcs = null;
		if (tfile != null) {
			trcs = (List<IGPSTrace>) FileUtilities.fileToObject(tfile);
			int idgen = 0;
			for (final IGPSTrace gps : trcs) {
				final IGPSTrace trace = gps.filter(ttime);
				if (trace.size() > 0) {
					if (useIds) {
						traces.put(trace.getId(), trace);
					} else {
						traces.put(idgen++, trace);
					}
				}
			}
			if (!useIds) {
				L.log("Traces available for " + idgen + " nodes.");
			}
		}
		forceStreets = onStreets;
		onlyStreet = onlyOnStreets;
		mapFile = file;
		initDir(mapfile);
		initAll();
	}
	
	private static void mkdirsIfNeeded(final File target) throws IOException {
		if (!target.exists()) {
			if (!target.mkdirs()) {
				throw new IOException("Can not create the required directory structure: " + target);
			}
		}
	}

	private void initAll() throws IOException {
		final File workdir = new File(dir);
		mkdirsIfNeeded(workdir);
		Arrays.stream(Vehicle.values()).parallel().forEach((v) -> {
			final String internalWorkdir = workdir + Global.SLASH + v;
			final File iwdf = new File(internalWorkdir);
			try {
				mkdirsIfNeeded(iwdf);
				final GraphHopper gh = new GraphHopper().forServer();
				gh.init(CmdArgs.read(new String[] {
						"graph.location=" + internalWorkdir,
						"osmreader.osm=" + mapFile,
						"osmreader.acceptWay=" + v }));
				gh.setCHShortcuts(ROUTING_STRATEGY);
				gh.importOrLoad();
				mapLock.write();
				navigators.put(v, gh);
				mapLock.release();
			} catch (IOException e) {
				L.error(e);
			}
		});
	}

	@Override
	public IRoute computeRoute(final INode<T> node, final INode<T> node2) {
		return computeRoute(node, getPosition(node2));
	}

	@Override
	public IRoute computeRoute(final IPosition p1, final IPosition p2) {
		return computeRoute(p1, p2, DEFAULT_VEHICLE);
	}

	@Override
	public IRoute computeRoute(final IPosition p1, final IPosition p2, final Vehicle vehicle) {
		final IRoute query = routecache.get(new Couple<>(p1, p2));
		if (query != null) {
			return query;
		}
		final GHRequest req = new GHRequest(p1.getCoordinate(1), p1.getCoordinate(0), p2.getCoordinate(1), p2.getCoordinate(0));
		req.setAlgorithm(DEFAULT_ALGORITHM);
		req.setVehicle(vehicle.toString());
		mapLock.read();
		final GHResponse resp = navigators.get(vehicle).route(req);
		mapLock.release();
		final IRoute result = new GraphHopperRoute(resp);
		routecache.put(new Couple<>(p1, p2), result);
		return result;
	}

	@Override
	public IRoute computeRoute(final INode<T> node, final IPosition coord) {
		return computeRoute(node, coord, DEFAULT_VEHICLE);
	}

	@Override
	public IRoute computeRoute(final INode<T> node, final IPosition coord, final Vehicle vehicle) {
		return computeRoute(getPosition(node), coord, vehicle);
	}

	private Optional<IPosition> getNearestStreetPoint(final IPosition position) {
		mapLock.read();
		final GraphHopper gh = navigators.get(Vehicle.BIKE);
		mapLock.release();
		final QueryResult qr = gh.getLocationIndex().findClosest(position.getCoordinate(1), position.getCoordinate(0), EdgeFilter.ALL_EDGES);
		if (qr.isValid()) {
			final GHPoint pt = qr.getSnappedPoint();
			return Optional.of(new LatLongPosition(pt.lat, pt.lon));
		}
		return Optional.empty();
	}

	/**
	 * @return the mapFile
	 */
	@Override
	public File getMapFile() {
		return new File(mapFile);
	}

	@Override
	public String getPreferredMonitor() {
		return MONITOR;
	}

	/**
	 * @return the minimum latitude
	 */
	protected double getMinLatitude() {
		return getOffset()[1];
	}

	/**
	 * @return the maximum latitude
	 */
	protected double getMaxLatitude() {
		return getOffset()[1] + getSize()[1];
	}

	/**
	 * @return the minimum longitude
	 */
	protected double getMinLongitude() {
		return getOffset()[0];
	}

	/**
	 * @return the maximum longitude
	 */
	protected double getMaxLongitude() {
		return getOffset()[0] + getSize()[0];
	}

	@Override
	public void addNode(final INode<T> node, final IPosition position) {
		Objects.requireNonNull(position, "The position cannot be null.");
		final IGPSTrace trace = traces.get(node.getId());
		if (trace == null) {
			final Optional<IPosition> computed = forceStreets ? getNearestStreetPoint(position) : Optional.of(position);
			if (computed.isPresent()) {
				super.addNode(node, computed.get());
			} else if (!onlyStreet) {
				super.addNode(node, position);
			}
		} else {
			super.addNode(node, trace.getPreviousPosition(0).toIPosition());
		}
	}

	@Override
	public IPosition getNextPosition(final INode<T> node, final ITime time) {
		final IGPSTrace trace = traces.get(node.getId());
		if (trace == null) {
			return getPosition(node);
		}
		return trace.getNextPosition(time.toDouble()).toIPosition();
	}

	@Override
	public IPosition getPreviousPosition(final INode<T> node, final ITime time) {
		final IGPSTrace trace = traces.get(node.getId());
		if (trace == null) {
			return getPosition(node);
		}
		return trace.getPreviousPosition(time.toDouble()).toIPosition();
	}

	@Override
	public IPosition getExpectedPosition(final INode<T> node, final ITime time) {
		final IGPSTrace trace = traces.get(node.getId());
		if (trace == null) {
			return getPosition(node);
		}
		return trace.interpolate(time.toDouble()).toIPosition();
	}

	@Override
	public IGPSTrace getTrace(final INode<T> node) {
		return traces.get(node.getId());
	}

	private void initDir(final File mapfile) throws IOException {
		final String code = Long.toString(FileUtilities.fileCRC32sum(mapfile), Global.ENCODING_BASE);
		dir = Global.PERSISTENTPATH + Global.SLASH + mapfile.getName() + code;
	}

	private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		final File f = new File(mapFile);
		if (!f.exists()) {
			throw new FileNotFoundException(mapFile + " does not exist.");
		}
		initDir(new File(mapFile));
		initAll();
	}

}
