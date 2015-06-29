/**
 * 
 */
package it.unibo.alchemist.model.implementations;

import it.unibo.alchemist.model.interfaces.IPosition;
import it.unibo.alchemist.model.interfaces.IRoute;

import java.util.List;

import org.danilopianini.lang.LangUtils;

import com.google.common.collect.Lists;

/**
 * Very simple route, the shortest path connecting the two passed points.
 * 
 * @author Danilo Pianini
 *
 */
public class PointToPointRoute implements IRoute {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6937104566388182150L;
	private final IPosition s, e;
	private double dist = Double.NaN;
	private List<IPosition> l; // Optional is not Serializable!

	/**
	 * 
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 */
	public PointToPointRoute(final IPosition start, final IPosition end) {
		LangUtils.requireNonNull(start, end);
		s = start;
		e = end;
	}

	@Override
	public double getDistance() {
		if (Double.isNaN(dist)) {
			dist = s.getDistanceTo(e);
		}
		return dist;
	}

	@Override
	public IPosition getPoint(final int step) {
		return step <= 0 ? s : e;
	}

	@Override
	public List<IPosition> getPoints() {
		if (l == null) {
			l = Lists.newArrayList(s, e);
		}
		return l;
	}

	@Override
	public int getPointsNumber() {
		return 2;
	}

	@Override
	public double getTime() {
		return Double.NaN;
	}

}
