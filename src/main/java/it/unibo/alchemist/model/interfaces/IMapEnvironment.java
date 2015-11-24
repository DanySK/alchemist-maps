/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.interfaces;

/**
 * @param <T>
 */
public interface IMapEnvironment<T> extends IEnvironment<T> {

    /**
     * The default vehicle.
     */
    Vehicle DEFAULT_VEHICLE = Vehicle.FOOT;

    /**
     * This method relies on the map data, and computes a route towards some
     * absolute coordinate solving a TSP problem. It's up to the specific
     * {@link IAction} calling this method to effectively move nodes along the
     * path. It uses the fastest path as metric.
     * 
     * @param node
     *            The start node
     * @param node2
     *            the second node's position will be used as destination
     * @return A {@link IRoute} object describing the path the node should
     *         follow
     */
    IRoute computeRoute(INode<T> node, INode<T> node2);

    /**
     * This method relies on the map data, and computes a route towards some
     * absolute coordinate solving a TSP problem. It's up to the specific
     * {@link IAction} calling this method to effectively move nodes along the
     * path.
     * 
     * @param p1
     *            start position
     * 
     * @param p2
     *            end position The absolute coordinate where this node wants to
     *            move to
     * @param vehicle
     *            vehicle to use. Different vehicles may use different paths,
     *            e.g. pedestrians can't go along a highway, but can walk the
     *            parks
     * @return A {@link IRoute} object describing the path the node should
     *         follow
     */
    IRoute computeRoute(IPosition p1, IPosition p2, final Vehicle vehicle);

    /**
     * This method relies on the map data, and computes a route towards some
     * absolute coordinate solving a TSP problem. It's up to the specific
     * {@link IAction} calling this method to effectively move nodes along the
     * path.
     * 
     * @param p1
     *            start position
     * 
     * @param p2
     *            end position The absolute coordinate where this node wants to
     *            move to
     * @return A {@link IRoute} object describing the path the node should
     *         follow
     */
    IRoute computeRoute(IPosition p1, IPosition p2);

    /**
     * This method relies on the map data, and computes a route towards some
     * absolute coordinate solving a TSP problem. It's up to the specific
     * {@link IAction} calling this method to effectively move nodes along the
     * path.
     * 
     * @param node
     *            The {@link INode} to move
     * @param coord
     *            The absolute coordinate where this node wants to move to
     * @return A {@link IRoute} object describing the path the node should
     *         follow
     */
    IRoute computeRoute(INode<T> node, IPosition coord);

    /**
     * This method relies on the map data, and computes a route towards some
     * absolute coordinate solving a TSP problem. It's up to the specific
     * {@link IAction} calling this method to effectively move nodes along the
     * path.
     * 
     * @param node
     *            The {@link INode} to move
     * @param coord
     *            The absolute coordinate where this node wants to move to
     * @param vehicle
     *            The vehicle tipe for this route
     * @return A {@link IRoute} object describing the path the node should
     *         follow
     */
    IRoute computeRoute(INode<T> node, IPosition coord, Vehicle vehicle);

    /**
     * Works only if the node is associated with a {@link IGPSTrace}.
     * 
     * @param node the {@link INode}
     * @param time the time
     * @return the position immediately after time in the {@link IGPSTrace}
     */
    IPosition getNextPosition(INode<T> node, ITime time);

    /**
     * Works only if the node is associated with a {@link IGPSTrace}.
     * 
     * @param node the {@link INode}
     * @param time the time
     * @return the position immediately before time in the {@link IGPSTrace}
     */
    IPosition getPreviousPosition(INode<T> node, ITime time);

    /**
     * Works only if the node is associated with a {@link IGPSTrace}.
     * 
     * @param node the {@link INode}
     * @param time the time
     * @return interpolates the position immediately before and the one immediately after time in the {@link IGPSTrace}
     */
    IPosition getExpectedPosition(INode<T> node, ITime time);

    /**
     * Works only if the node is associated with a {@link IGPSTrace}.
     * @param node the {@link INode}
     * @return the associated {@link IGPSTrace}
     */
    IGPSTrace getTrace(INode<T> node);
}
