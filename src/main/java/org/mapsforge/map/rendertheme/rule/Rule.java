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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapsforge.core.model.Tag;
import org.mapsforge.map.rendertheme.RenderCallback;
import org.mapsforge.map.rendertheme.renderinstruction.RenderInstruction;

public abstract class Rule {
    public static final Map<List<String>, AttributeMatcher> MATCHERS_CACHE_KEY = new HashMap<List<String>, AttributeMatcher>();
    public static final Map<List<String>, AttributeMatcher> MATCHERS_CACHE_VALUE = new HashMap<List<String>, AttributeMatcher>();

    private final List<RenderInstruction> renderInstructions;
    private final List<Rule> subRules;
    private final ClosedMatcher closedMatcher;
    private final ElementMatcher elementMatcher;
    private final byte zoomMax;
    private final byte zoomMin;

    public Rule(final RuleBuilder ruleBuilder) {
        this.closedMatcher = ruleBuilder.getClosedMatcher();
        this.elementMatcher = ruleBuilder.getElementMatcher();
        this.zoomMax = ruleBuilder.getZoomMax();
        this.zoomMin = ruleBuilder.getZoomMin();

        this.renderInstructions = new ArrayList<RenderInstruction>(4);
        this.subRules = new ArrayList<Rule>(4);
    }

    public void addRenderingInstruction(final RenderInstruction renderInstruction) {
        this.renderInstructions.add(renderInstruction);
    }

    public void addSubRule(final Rule rule) {
        this.subRules.add(rule);
    }

    public ClosedMatcher getClosedMatcher() {
        return closedMatcher;
    }

    public ElementMatcher getElementMatcher() {
        return elementMatcher;
    }

    public List<RenderInstruction> getRenderInstructions() {
        return renderInstructions;
    }

    public List<Rule> getSubRules() {
        return subRules;
    }

    public byte getZoomMax() {
        return zoomMax;
    }

    public byte getZoomMin() {
        return zoomMin;
    }

    public abstract boolean matchesNode(List<Tag> tags, byte zoomLevel);

    public abstract boolean matchesWay(List<Tag> tags, byte zoomLevel, Closed closed);

    public void matchNode(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel) {
        if (matchesNode(tags, zoomLevel)) {
            int n = this.renderInstructions.size();
            for (int i = 0; i < n; ++i) {
                this.renderInstructions.get(i).renderNode(renderCallback, tags);
            }
            n = this.subRules.size();
            for (int i = 0; i < n; ++i) {
                this.subRules.get(i).matchNode(renderCallback, tags, zoomLevel);
            }
        }
    }

    public void matchWay(final RenderCallback renderCallback, final List<Tag> tags, final byte zoomLevel, final Closed closed, final List<RenderInstruction> matchingList) {
        if (matchesWay(tags, zoomLevel, closed)) {
            int n = this.renderInstructions.size();
            for (int i = 0; i < n; ++i) {
                this.renderInstructions.get(i).renderWay(renderCallback, tags);
                matchingList.add(this.renderInstructions.get(i));
            }
            n = this.subRules.size();
            for (int i = 0; i < n; ++i) {
                this.subRules.get(i).matchWay(renderCallback, tags, zoomLevel, closed, matchingList);
            }
        }
    }

    public void onComplete() {
        MATCHERS_CACHE_KEY.clear();
        MATCHERS_CACHE_VALUE.clear();

        ((ArrayList<RenderInstruction>) this.renderInstructions).trimToSize();
        ((ArrayList<Rule>) this.subRules).trimToSize();
        final int n = this.subRules.size();
        for (int i = 0; i < n; ++i) {
            this.subRules.get(i).onComplete();
        }
    }

    public void scaleStrokeWidth(final float scaleFactor) {
        int n = this.renderInstructions.size();
        for (int i = 0; i < n; ++i) {
            this.renderInstructions.get(i).scaleStrokeWidth(scaleFactor);
        }
        n = this.subRules.size();
        for (int i = 0; i < n; ++i) {
            this.subRules.get(i).scaleStrokeWidth(scaleFactor);
        }
    }

    public void scaleTextSize(final float scaleFactor) {
        int n = this.renderInstructions.size();
        for (int i = 0; i < n; ++i) {
            this.renderInstructions.get(i).scaleTextSize(scaleFactor);
        }
        n = this.subRules.size();
        for (int i = 0; i < n; ++i) {
            this.subRules.get(i).scaleTextSize(scaleFactor);
        }
    }
}
