/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.graphics.Path;
import org.mapsforge.core.model.Point;

public class CanvasRasterer {
    private final Canvas canvas;
    private final Path path;
    private final Matrix symbolMatrix;

    CanvasRasterer(final GraphicFactory graphicFactory) {
        this.canvas = graphicFactory.createCanvas();
        this.symbolMatrix = graphicFactory.createMatrix();
        this.path = graphicFactory.createPath();
    }

    public void drawNodes(final List<PointTextContainer> pointTextContainers) {
        for (int index = pointTextContainers.size() - 1; index >= 0; --index) {
            final PointTextContainer pointTextContainer = pointTextContainers.get(index);

            if (pointTextContainer.getPaintBack() != null) {
                this.canvas.drawText(pointTextContainer.getText(), (int) pointTextContainer.getX(), (int) pointTextContainer.getY(), pointTextContainer.getPaintBack());
            }

            this.canvas.drawText(pointTextContainer.getText(), (int) pointTextContainer.getX(), (int) pointTextContainer.getY(), pointTextContainer.getPaintFront());
        }
    }

    public void drawSymbols(final List<SymbolContainer> symbolContainers) {
        for (int index = symbolContainers.size() - 1; index >= 0; --index) {
            final SymbolContainer symbolContainer = symbolContainers.get(index);

            final Point point = symbolContainer.getPoint();
            this.symbolMatrix.reset();

            if (symbolContainer.isAlignCenter()) {
                final int pivotX = symbolContainer.getSymbol().getWidth() / 2;
                final int pivotY = symbolContainer.getSymbol().getHeight() / 2;
                this.symbolMatrix.translate((float) (point.getX() - pivotX), (float) (point.getY() - pivotY));
                this.symbolMatrix.rotate(symbolContainer.getTheta(), pivotX, pivotY);
            } else {
                this.symbolMatrix.translate((float) point.getX(), (float) point.getY());
                this.symbolMatrix.rotate(symbolContainer.getTheta());
            }

            this.canvas.drawBitmap(symbolContainer.getSymbol(), this.symbolMatrix);
        }
    }

    public void drawWayNames(final List<WayTextContainer> wayTextContainers) {
        for (int index = wayTextContainers.size() - 1; index >= 0; --index) {
            final WayTextContainer wayTextContainer = wayTextContainers.get(index);
            this.canvas.drawTextRotated(wayTextContainer.getText(), wayTextContainer.getX1(), wayTextContainer.getY1(), wayTextContainer.getX2(), wayTextContainer.getY2(), wayTextContainer.getPaint());
        }
    }

    public void drawWays(final List<List<List<ShapePaintContainer>>> drawWays) {
        final int levelsPerLayer = drawWays.get(0).size();
        int l;
        final int layers = drawWays.size();
        for (l = 0; l < layers; l++) {
            final List<List<ShapePaintContainer>> shapePaintContainers = drawWays.get(l);

            for (int level = 0; level < levelsPerLayer; ++level) {
                final List<ShapePaintContainer> wayList = shapePaintContainers.get(level);

                for (int index = wayList.size() - 1; index >= 0; --index) {
                    final ShapePaintContainer shapePaintContainer = wayList.get(index);
                    this.path.clear();
                    final ShapeType st = shapePaintContainer.getShapeContainer().getShapeType();
                    if (st.equals(ShapeType.CIRCLE)) {
                        final CircleContainer circleContainer = (CircleContainer) shapePaintContainer.getShapeContainer();
                        final Point point = circleContainer.getPoint();

                        this.canvas.drawCircle((int) point.getX(), (int) point.getY(), (int) circleContainer.getRadius(), shapePaintContainer.getPaint());
                    } else if (st.equals(ShapeType.WAY)) {
                        final WayContainer wayContainer = (WayContainer) shapePaintContainer.getShapeContainer();
                        final Point[][] coordinates = wayContainer.getCoordinates();
                        for (final Point[] points : coordinates) {
                            if (points.length >= 2) {
                                Point immutablePoint = points[0];
                                this.path.addPoint((int) immutablePoint.getX(), (int) immutablePoint.getY());
                                for (int i = 1; i < points.length; ++i) {
                                    immutablePoint = points[i];
                                    this.path.addPoint((int) immutablePoint.getX(), (int) immutablePoint.getY());
                                }
                            }
                        }
                    }
                    this.canvas.drawPath(this.path, shapePaintContainer.getPaint());
                }
            }
        }
    }

    public void fill(final int color) {
        this.canvas.fillColor(color);
    }

    public void setCanvasBitmap(final Bitmap bitmap) {
        this.canvas.setBitmap(bitmap);
    }
}
