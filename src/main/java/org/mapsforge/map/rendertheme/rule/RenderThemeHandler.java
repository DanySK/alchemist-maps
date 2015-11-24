/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.rendertheme.rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.util.IOUtils;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.mapsforge.map.rendertheme.renderinstruction.Area;
import org.mapsforge.map.rendertheme.renderinstruction.AreaBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.Caption;
import org.mapsforge.map.rendertheme.renderinstruction.CaptionBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.Circle;
import org.mapsforge.map.rendertheme.renderinstruction.CircleBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.Line;
import org.mapsforge.map.rendertheme.renderinstruction.LineBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.LineSymbol;
import org.mapsforge.map.rendertheme.renderinstruction.LineSymbolBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.PathText;
import org.mapsforge.map.rendertheme.renderinstruction.PathTextBuilder;
import org.mapsforge.map.rendertheme.renderinstruction.Symbol;
import org.mapsforge.map.rendertheme.renderinstruction.SymbolBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX2 handler to parse XML render theme files.
 */
public final class RenderThemeHandler extends DefaultHandler {
    private static final String ELEMENT_NAME_RULE = "rule";

    private static final Logger LOGGER = Logger.getLogger(RenderThemeHandler.class.getName());
    private static final String UNEXPECTED_ELEMENT = "unexpected element: ";
    private Rule currentRule;

    private final Stack<Element> elementStack = new Stack<Element>();

    private final GraphicFactory graphicFactory;
    private int level;
    private final String relativePathPrefix;
    private RenderTheme renderTheme;
    private final Stack<Rule> ruleStack = new Stack<Rule>();

    private static enum Element {
        RENDER_THEME, RENDERING_INSTRUCTION, RULE;
    }

    public static RenderTheme getRenderTheme(final GraphicFactory graphicFactory, final XmlRenderTheme xmlRenderTheme) throws SAXException, ParserConfigurationException, IOException {
        final RenderThemeHandler renderThemeHandler = new RenderThemeHandler(graphicFactory, xmlRenderTheme.getRelativePathPrefix());
        final XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        xmlReader.setContentHandler(renderThemeHandler);
        InputStream inputStream = null;
        try {
            inputStream = xmlRenderTheme.getRenderThemeAsStream();
            xmlReader.parse(new InputSource(inputStream));
            return renderThemeHandler.renderTheme;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private RenderThemeHandler(final GraphicFactory graphicFactory, final String relativePathPrefix) {
        super();
        this.graphicFactory = graphicFactory;
        this.relativePathPrefix = relativePathPrefix;
    }

    private void checkElement(final String elementName, final Element element) throws SAXException {
        switch (element) {
        case RENDER_THEME:
            if (!this.elementStack.empty()) {
                throw new SAXException(UNEXPECTED_ELEMENT + elementName);
            }
            return;

        case RULE:
            final Element parentElement = this.elementStack.peek();
            if (parentElement != Element.RENDER_THEME && parentElement != Element.RULE) {
                throw new SAXException(UNEXPECTED_ELEMENT + elementName);
            }
            return;

        case RENDERING_INSTRUCTION:
            if (this.elementStack.peek() != Element.RULE) {
                throw new SAXException(UNEXPECTED_ELEMENT + elementName);
            }
            return;
        default:
            break;
        }

        throw new SAXException("unknown enum value: " + element);
    }

    private void checkState(final String elementName, final Element element) throws SAXException {
        checkElement(elementName, element);
        this.elementStack.push(element);
    }

    @Override
    public void endDocument() {
        if (this.renderTheme == null) {
            throw new IllegalArgumentException("missing element: rules");
        }

        this.renderTheme.setLevels(this.level);
        this.renderTheme.complete();
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        this.elementStack.pop();

        if (ELEMENT_NAME_RULE.equals(qName)) {
            this.ruleStack.pop();
            if (this.ruleStack.empty()) {
                this.renderTheme.addRule(this.currentRule);
            } else {
                this.currentRule = this.ruleStack.peek();
            }
        }
    }

    @Override
    public void error(final SAXParseException exception) {
        LOGGER.log(Level.SEVERE, null, exception);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        try {
            if ("rendertheme".equals(qName)) {
                checkState(qName, Element.RENDER_THEME);
                this.renderTheme = new RenderThemeBuilder(this.graphicFactory, qName, attributes).build();
            } else if (ELEMENT_NAME_RULE.equals(qName)) {
                checkState(qName, Element.RULE);
                final Rule rule = new RuleBuilder(qName, attributes, this.ruleStack).build();
                if (!this.ruleStack.empty()) {
                    this.currentRule.addSubRule(rule);
                }
                this.currentRule = rule;
                this.ruleStack.push(this.currentRule);
            } else if ("area".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final Area area = new AreaBuilder(this.graphicFactory, qName, attributes, this.level++, this.relativePathPrefix).build();
                this.currentRule.addRenderingInstruction(area);
            } else if ("caption".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final Caption caption = new CaptionBuilder(this.graphicFactory, qName, attributes).build();
                this.currentRule.addRenderingInstruction(caption);
            } else if ("circle".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final Circle circle = new CircleBuilder(this.graphicFactory, qName, attributes, this.level++).build();
                this.currentRule.addRenderingInstruction(circle);
            } else if ("line".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final Line line = new LineBuilder(this.graphicFactory, qName, attributes, this.level++, this.relativePathPrefix).build();
                this.currentRule.addRenderingInstruction(line);
            } else if ("lineSymbol".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final LineSymbol lineSymbol = new LineSymbolBuilder(this.graphicFactory, qName, attributes, this.relativePathPrefix).build();
                this.currentRule.addRenderingInstruction(lineSymbol);
            } else if ("pathText".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final PathText pathText = new PathTextBuilder(this.graphicFactory, qName, attributes).build();
                this.currentRule.addRenderingInstruction(pathText);
            } else if ("symbol".equals(qName)) {
                checkState(qName, Element.RENDERING_INSTRUCTION);
                final Symbol symbol = new SymbolBuilder(this.graphicFactory, qName, attributes, this.relativePathPrefix).build();
                this.currentRule.addRenderingInstruction(symbol);
            } else {
                throw new SAXException("unknown element: " + qName);
            }
        } catch (final IOException e) {
            throw new SAXException(null, e);
        }
    }

    @Override
    public void warning(final SAXParseException exception) {
        LOGGER.log(Level.SEVERE, null, exception);
    }
}
