/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.Tag;
import org.mapsforge.core.util.LRUCache;
import org.mapsforge.map.rendertheme.RenderCallback;
import org.mapsforge.map.rendertheme.renderinstruction.RenderInstruction;

/**
 * A RenderTheme defines how ways and nodes are drawn.
 */
public class RenderTheme {
    private static final int MATCHING_CACHE_SIZE = 512;

    private final float baseStrokeWidth;
    private final float baseTextSize;
    private int levels;
    private final int mapBackground;
    private final LRUCache<MatchingCacheKey, List<RenderInstruction>> matchingCache;
    private final List<Rule> rulesList;

    public RenderTheme(final RenderThemeBuilder renderThemeBuilder) {
        this.baseStrokeWidth = renderThemeBuilder.getBaseStrokeWidth();
        this.baseTextSize = renderThemeBuilder.getBaseTextSize();
        this.mapBackground = renderThemeBuilder.getMapBackground();
        this.rulesList = new ArrayList<Rule>();
        this.matchingCache = new LRUCache<MatchingCacheKey, List<RenderInstruction>>(MATCHING_CACHE_SIZE);
    }

    public void addRule(final Rule rule) {
        this.rulesList.add(rule);
    }

    public void complete() {
        ((ArrayList<Rule>) this.rulesList).trimToSize();
        final int n = this.rulesList.size();
        for (int i = 0; i < n; ++i) {
            this.rulesList.get(i).onComplete();
        }
    }

    /**
     * Must be called when this RenderTheme gets destroyed to clean up and free
     * resources.
     */
    public void destroy() {
        this.matchingCache.clear();
    }

    /**
     * @return the number of distinct drawing levels required by this
     *         RenderTheme.
     */
    public int getLevels() {
        return this.levels;
    }

    /**
     * @return the map background color of this RenderTheme.
     */
    public int getMapBackground() {
        return this.mapBackground;
    }

    /**
     * Matches a closed way with the given parameters against this RenderTheme.
     * 
     * @param renderCallback
     *            the callback implementation which will be executed on each
     *            match.
     * @param tags
     *            the tags of the way.
     * @param zoomLevel
     *            the zoom level at which the way should be matched.
     */
    public void matchClosedWay(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel) {
        matchWay(renderCallback, tags, zoomLevel, Closed.YES);
    }

    /**
     * Matches a linear way with the given parameters against this RenderTheme.
     * 
     * @param renderCallback
     *            the callback implementation which will be executed on each
     *            match.
     * @param tags
     *            the tags of the way.
     * @param zoomLevel
     *            the zoom level at which the way should be matched.
     */
    public void matchLinearWay(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel) {
        matchWay(renderCallback, tags, zoomLevel, Closed.NO);
    }

    /**
     * Matches a node with the given parameters against this RenderTheme.
     * 
     * @param renderCallback
     *            the callback implementation which will be executed on each
     *            match.
     * @param tags
     *            the tags of the node.
     * @param zoomLevel
     *            the zoom level at which the node should be matched.
     */
    public void matchNode(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel) {
        final int n = this.rulesList.size();
        for (int i = 0; i < n; ++i) {
            this.rulesList.get(i).matchNode(renderCallback, tags, zoomLevel);
        }
    }

    private void matchWay(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel, final Closed closed) {
        final MatchingCacheKey matchingCacheKey = new MatchingCacheKey(tags, zoomLevel, closed);

        List<RenderInstruction> matchingList = this.matchingCache.get(matchingCacheKey);
        if (matchingList != null) {
            // cache hit
            final int n = matchingList.size();
            for (int i = 0; i < n; ++i) {
                matchingList.get(i).renderWay(renderCallback, tags);
            }
            return;
        }

        // cache miss
        matchingList = new ArrayList<RenderInstruction>();
        final int n = this.rulesList.size();
        for (int i = 0; i < n; ++i) {
            this.rulesList.get(i).matchWay(renderCallback, tags, zoomLevel, closed, matchingList);
        }

        this.matchingCache.put(matchingCacheKey, matchingList);
    }

    /**
     * Scales the stroke width of this RenderTheme by the given factor.
     * 
     * @param scaleFactor
     *            the factor by which the stroke width should be scaled.
     */
    public void scaleStrokeWidth(final float scaleFactor) {
        final int n = this.rulesList.size();
        for (int i = 0; i < n; ++i) {
            this.rulesList.get(i).scaleStrokeWidth(scaleFactor * this.baseStrokeWidth);
        }
    }

    /**
     * Scales the text size of this RenderTheme by the given factor.
     * 
     * @param scaleFactor
     *            the factor by which the text size should be scaled.
     */
    public void scaleTextSize(final float scaleFactor) {
        final int n = this.rulesList.size();
        for (int i = 0; i < n; ++i) {
            this.rulesList.get(i).scaleTextSize(scaleFactor * this.baseTextSize);
        }
    }

    public void setLevels(final int levels) {
        this.levels = levels;
    }
}
