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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Pattern;

import org.mapsforge.map.rendertheme.XmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A builder for {@link Rule} instances.
 */
public class RuleBuilder {
    private static final String CLOSED = "closed";
    private static final String E = "e";
    private static final String K = "k";
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\|");
    private static final String STRING_NEGATION = "~";
    private static final String STRING_WILDCARD = "*";
    private static final String V = "v";
    private static final String ZOOM_MAX = "zoom-max";
    private static final String ZOOM_MIN = "zoom-min";

    private Closed closed;

    private Element element;

    private List<String> keyList;

    private String keys;

    private final Stack<Rule> ruleStack;
    private List<String> valueList;
    private String values;
    private ClosedMatcher closedMatcher;
    private ElementMatcher elementMatcher;
    private byte zoomMax;
    private byte zoomMin;

    private static ClosedMatcher getClosedMatcher(final Closed closed) {
        switch (closed) {
        case YES:
            return ClosedWayMatcher.getInstance();
        case NO:
            return LinearWayMatcher.getInstance();
        case ANY:
            return AnyMatcher.getInstance();
        default:
            throw new IllegalArgumentException("unknown closed value: " + closed);
        }
    }

    private static ElementMatcher getElementMatcher(final Element element) {
        switch (element) {
        case NODE:
            return ElementNodeMatcher.getInstance();
        case WAY:
            return ElementWayMatcher.getInstance();
        case ANY:
            return AnyMatcher.getInstance();
        default:
            throw new IllegalArgumentException("unknown element value: " + element);
        }
    }

    private static AttributeMatcher getKeyMatcher(final List<String> keyList) {
        if (STRING_WILDCARD.equals(keyList.get(0))) {
            return AnyMatcher.getInstance();
        }

        AttributeMatcher attributeMatcher = Rule.MATCHERS_CACHE_KEY.get(keyList);
        if (attributeMatcher == null) {
            attributeMatcher = new KeyMatcher(keyList);
            Rule.MATCHERS_CACHE_KEY.put(keyList, attributeMatcher);
        }
        return attributeMatcher;
    }

    private static AttributeMatcher getValueMatcher(final List<String> valueList) {
        if (STRING_WILDCARD.equals(valueList.get(0))) {
            return AnyMatcher.getInstance();
        }

        AttributeMatcher attributeMatcher = Rule.MATCHERS_CACHE_VALUE.get(valueList);
        if (attributeMatcher == null) {
            attributeMatcher = new ValueMatcher(valueList);
            Rule.MATCHERS_CACHE_VALUE.put(valueList, attributeMatcher);
        }
        return attributeMatcher;
    }

    public RuleBuilder(final String elementName, final Attributes attributes, final Stack<Rule> ruleStack) throws SAXException {
        this.ruleStack = ruleStack;

        this.closed = Closed.ANY;
        this.zoomMin = 0;
        this.zoomMax = Byte.MAX_VALUE;

        extractValues(elementName, attributes);
    }

    /**
     * @return a new {@code Rule} instance.
     */
    public Rule build() {
        if (this.valueList.remove(STRING_NEGATION)) {
            final AttributeMatcher attributeMatcher = new NegativeMatcher(this.keyList, this.valueList);
            return new NegativeRule(this, attributeMatcher);
        }

        AttributeMatcher keyMatcher = getKeyMatcher(this.keyList);
        AttributeMatcher valueMatcher = getValueMatcher(this.valueList);

        keyMatcher = RuleOptimizer.optimize(keyMatcher, this.ruleStack);
        valueMatcher = RuleOptimizer.optimize(valueMatcher, this.ruleStack);

        return new PositiveRule(this, keyMatcher, valueMatcher);
    }

    private void extractValues(final String elementName, final Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (E.equals(name)) {
                this.element = Element.valueOf(value.toUpperCase(Locale.ENGLISH));
            } else if (K.equals(name)) {
                this.keys = value;
            } else if (V.equals(name)) {
                this.values = value;
            } else if (CLOSED.equals(name)) {
                this.closed = Closed.valueOf(value.toUpperCase(Locale.ENGLISH));
            } else if (ZOOM_MIN.equals(name)) {
                this.zoomMin = XmlUtils.parseNonNegativeByte(name, value);
            } else if (ZOOM_MAX.equals(name)) {
                this.zoomMax = XmlUtils.parseNonNegativeByte(name, value);
            } else {
                throw XmlUtils.createSAXException(elementName, name, value, i);
            }
        }

        validate(elementName);

        this.keyList = new ArrayList<String>(Arrays.asList(SPLIT_PATTERN.split(this.keys)));
        this.valueList = new ArrayList<String>(Arrays.asList(SPLIT_PATTERN.split(this.values)));

        this.elementMatcher = getElementMatcher(this.element);
        this.closedMatcher = getClosedMatcher(this.closed);

        this.elementMatcher = RuleOptimizer.optimize(this.elementMatcher, this.ruleStack);
        this.closedMatcher = RuleOptimizer.optimize(this.closedMatcher, this.ruleStack);
    }

    public ClosedMatcher getClosedMatcher() {
        return closedMatcher;
    }

    public ElementMatcher getElementMatcher() {
        return elementMatcher;
    }

    public byte getZoomMax() {
        return zoomMax;
    }

    public byte getZoomMin() {
        return zoomMin;
    }

    public void setClosedMatcher(final ClosedMatcher closedMatcher) {
        this.closedMatcher = closedMatcher;
    }

    public void setElementMatcher(final ElementMatcher elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    public void setZoomMax(final byte zoomMax) {
        this.zoomMax = zoomMax;
    }

    public void setZoomMin(final byte zoomMin) {
        this.zoomMin = zoomMin;
    }

    private void validate(final String elementName) throws SAXException {
        XmlUtils.checkMandatoryAttribute(elementName, E, this.element);
        XmlUtils.checkMandatoryAttribute(elementName, K, this.keys);
        XmlUtils.checkMandatoryAttribute(elementName, V, this.values);

        if (this.zoomMin > this.zoomMax) {
            throw new SAXException('\'' + ZOOM_MIN + "' > '" + ZOOM_MAX + "': " + this.zoomMin + ' ' + this.zoomMax);
        }
    }
}
