/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.model.Tile;

/**
 * This class place the labels form POIs, area labels and normal labels. The
 * main target is avoiding collisions of these different labels.
 */
public class LabelPlacement {
    private static final int LABEL_DISTANCE_TO_LABEL = 2;

    private static final int LABEL_DISTANCE_TO_SYMBOL = 2;

    private static final int START_DISTANCE_TO_SYMBOLS = 4;

    private static final int SYMBOL_DISTANCE_TO_SYMBOL = 2;

    private final DependencyCache dependencyCache;

    private PointTextContainer label;
    private Rectangle rect1;
    private Rectangle rect2;
    private ReferencePosition referencePosition;

    private SymbolContainer symbolContainer;

    /**
     * This class holds the reference positions for the two and four point
     * greedy algorithms.
     */
    public static class ReferencePosition {
        double height;
        final int nodeNumber;
        SymbolContainer symbol;
        double width;
        final double x;
        final double y;

        public ReferencePosition(final double x, final double y, final int nodeNumber, final double width, final double height, final SymbolContainer symbol) {
            this.x = x;
            this.y = y;
            this.nodeNumber = nodeNumber;
            this.width = width;
            this.height = height;
            this.symbol = symbol;
        }
    }

    private static final class ReferencePositionHeightComparator implements Comparator<ReferencePosition>, Serializable {
        private static final long serialVersionUID = 1L;
        private static final ReferencePositionHeightComparator INSTANCE = new ReferencePositionHeightComparator();

        private ReferencePositionHeightComparator() {
            // do nothing
        }

        @Override
        public int compare(final ReferencePosition x, final ReferencePosition y) {
            if (x.y - x.height < y.y - y.height) {
                return -1;
            }

            if (x.y - x.height > y.y - y.height) {
                return 1;
            }
            return 0;
        }

        @Override
        public Comparator<ReferencePosition> reversed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparing(final Comparator<? super ReferencePosition> other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor, final Comparator<? super U> keyComparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingInt(final ToIntFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingLong(final ToLongFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingDouble(final ToDoubleFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

    }

    private static final class ReferencePositionWidthComparator implements Comparator<ReferencePosition>, Serializable {
        private static final long serialVersionUID = 1L;
        private static final ReferencePositionWidthComparator INSTANCE = new ReferencePositionWidthComparator();

        private ReferencePositionWidthComparator() {
            // do nothing
        }

        @Override
        public int compare(final ReferencePosition x, final ReferencePosition y) {
            if (x.x + x.width < y.x + y.width) {
                return -1;
            }

            if (x.x + x.width > y.x + y.width) {
                return 1;
            }

            return 0;
        }

        @Override
        public Comparator<ReferencePosition> reversed() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Comparator<ReferencePosition> thenComparing(final Comparator<? super ReferencePosition> other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor, final Comparator<? super U> keyComparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingInt(final ToIntFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingLong(final ToLongFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingDouble(final ToDoubleFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

    }

    private static final class ReferencePositionXComparator implements Comparator<ReferencePosition>, Serializable {
        private static final long serialVersionUID = 1L;
        static final ReferencePositionXComparator INSTANCE = new ReferencePositionXComparator();

        private ReferencePositionXComparator() {
            // do nothing
        }

        @Override
        public int compare(final ReferencePosition x, final ReferencePosition y) {
            if (x.x < y.x) {
                return -1;
            }

            if (x.x > y.x) {
                return 1;
            }

            return 0;
        }

        @Override
        public Comparator<ReferencePosition> reversed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparing(final Comparator<? super ReferencePosition> other) {
            throw new UnsupportedOperationException();
        }

        @Override 
        public <U> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor, final Comparator<? super U> keyComparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingInt(final ToIntFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingLong(final ToLongFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingDouble(final ToDoubleFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

    }

    private static final class ReferencePositionYComparator implements Comparator<ReferencePosition>, Serializable {
        private static final long serialVersionUID = 1L;
        static final ReferencePositionYComparator INSTANCE = new ReferencePositionYComparator();

        private ReferencePositionYComparator() {
            // do nothing
        }

        @Override
        public int compare(final ReferencePosition x, final ReferencePosition y) {
            if (x.y < y.y) {
                return -1;
            }

            if (x.y > y.y) {
                return 1;
            }

            return 0;
        }

        @Override
        public Comparator<ReferencePosition> reversed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparing(final Comparator<? super ReferencePosition> other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor, final Comparator<? super U> keyComparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<ReferencePosition> thenComparing(final Function<? super ReferencePosition, ? extends U> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingInt(final ToIntFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingLong(final ToLongFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<ReferencePosition> thenComparingDouble(final ToDoubleFunction<? super ReferencePosition> keyExtractor) {
            throw new UnsupportedOperationException();
        }

    }

    public LabelPlacement() {
        this.dependencyCache = new DependencyCache();
    }

    /**
     * Centers the labels.
     * 
     * @param labels
     *            labels to center
     */
    private void centerLabels(final List<PointTextContainer> labels) {
        for (int i = 0; i < labels.size(); i++) {
            this.label = labels.get(i);
            this.label.setX(this.label.getX() - this.label.getBoundary().getWidth() / 2);
        }
    }

    /**
     * The inputs are all the label and symbol objects of the current object.
     * The output is overlap free label and symbol placement with the greedy
     * strategy. The placement model is either the two fixed point or the four
     * fixed point model.
     * 
     * @param labels
     *            labels from the current object.
     * @param symbols
     *            symbols of the current object.
     * @param areaLabels
     *            area labels from the current object.
     * @param cT
     *            current object with the x,y- coordinates and the zoom level.
     * @return the processed list of labels.
     */
    public List<PointTextContainer> placeLabels(final List<PointTextContainer> labels, final List<SymbolContainer> symbols, final List<PointTextContainer> areaLabels, final Tile cT) {
        List<PointTextContainer> returnLabels = labels;
        this.dependencyCache.generateTileAndDependencyOnTile(cT);

        preprocessAreaLabels(areaLabels);

        preprocessLabels(returnLabels);

        preprocessSymbols(symbols);

        removeEmptySymbolReferences(returnLabels, symbols);

        removeOverlappingSymbolsWithAreaLabels(symbols, areaLabels);

        this.dependencyCache.removeOverlappingObjectsWithDependencyOnTile(returnLabels, areaLabels, symbols);

        if (!returnLabels.isEmpty()) {
            returnLabels = processFourPointGreedy(returnLabels, symbols, areaLabels);
        }

        this.dependencyCache.fillDependencyOnTile(returnLabels, symbols, areaLabels);

        return returnLabels;
    }

    private void preprocessAreaLabels(final List<PointTextContainer> areaLabels) {
        centerLabels(areaLabels);

        removeOutOfTileAreaLabels(areaLabels);

        removeOverlappingAreaLabels(areaLabels);

        if (!areaLabels.isEmpty()) {
            this.dependencyCache.removeAreaLabelsInAlreadyDrawnAreas(areaLabels);
        }
    }

    private void preprocessLabels(final List<PointTextContainer> labels) {
        removeOutOfTileLabels(labels);
    }

    private void preprocessSymbols(final List<SymbolContainer> symbols) {
        removeOutOfTileSymbols(symbols);
        removeOverlappingSymbols(symbols);
        this.dependencyCache.removeSymbolsFromDrawnAreas(symbols);
    }

    /**
     * This method uses an adapted greedy strategy for the fixed four position
     * model, above, under left and right form the point of interest. It uses no
     * priority search tree, because it will not function with symbols only with
     * points. Instead it uses two minimum heaps. They work similar to a sweep
     * line algorithm but have not a O(n log n +k) runtime. To find the
     * rectangle that has the top edge, I use also a minimum Heap. The
     * rectangles are sorted by their y coordinates.
     * 
     * @param labels
     *            label positions and text
     * @param symbols
     *            symbol positions
     * @param areaLabels
     *            area label positions and text
     * @return list of labels without overlaps with symbols and other labels by
     *         the four fixed position greedy strategy
     */
    private List<PointTextContainer> processFourPointGreedy(final List<PointTextContainer> labels, final List<SymbolContainer> symbols, final List<PointTextContainer> areaLabels) {
        final List<PointTextContainer> resolutionSet = new ArrayList<PointTextContainer>();

        // Array for the generated reference positions around the points of
        // interests
        final ReferencePosition[] refPos = new ReferencePosition[labels.size() * 4];

        // lists that sorts the reference points after the minimum top edge y
        // position
        final PriorityQueue<ReferencePosition> priorUp = new PriorityQueue<ReferencePosition>(labels.size() * 4 * 2 + labels.size() / 10 * 2, ReferencePositionYComparator.INSTANCE);
        // lists that sorts the reference points after the minimum bottom edge y
        // position
        final PriorityQueue<ReferencePosition> priorDown = new PriorityQueue<ReferencePosition>(labels.size() * 4 * 2 + labels.size() / 10 * 2, ReferencePositionHeightComparator.INSTANCE);

        PointTextContainer tmp;
        final int dis = START_DISTANCE_TO_SYMBOLS;

        // creates the reference positions
        for (int z = 0; z < labels.size(); z++) {
            if (labels.get(z) != null) {
                if (labels.get(z).getSymbol() != null) {
                    tmp = labels.get(z);

                    // up
                    refPos[z * 4] = new ReferencePosition(tmp.getX() - tmp.getBoundary().getWidth() / 2, tmp.getY() - tmp.getSymbol().getSymbol().getHeight() / 2 - dis, z, tmp.getBoundary().getWidth(), tmp.getBoundary().getHeight(), tmp.getSymbol());
                    // down
                    refPos[z * 4 + 1] = new ReferencePosition(tmp.getX() - tmp.getBoundary().getWidth() / 2, tmp.getY() + tmp.getSymbol().getSymbol().getHeight() / 2 + tmp.getBoundary().getHeight() + dis, z, tmp.getBoundary().getWidth(), tmp.getBoundary().getHeight(), tmp.getSymbol());
                    // left
                    refPos[z * 4 + 2] = new ReferencePosition(tmp.getX() - tmp.getSymbol().getSymbol().getWidth() / 2 - tmp.getBoundary().getWidth() - dis, tmp.getY() + tmp.getBoundary().getHeight() / 2, z, tmp.getBoundary().getWidth(), tmp.getBoundary().getHeight(), tmp.getSymbol());
                    // right
                    refPos[z * 4 + 3] = new ReferencePosition(tmp.getX() + tmp.getSymbol().getSymbol().getWidth() / 2 + dis, tmp.getY() + tmp.getBoundary().getHeight() / 2 - 0.1f, z, tmp.getBoundary().getWidth(), tmp.getBoundary().getHeight(), tmp.getSymbol());
                } else {
                    refPos[z * 4] = new ReferencePosition(labels.get(z).getX() - labels.get(z).getBoundary().getWidth() / 2, labels.get(z).getY(), z, labels.get(z).getBoundary().getWidth(), labels.get(z).getBoundary().getHeight(), null);
                    refPos[z * 4 + 1] = null;
                    refPos[z * 4 + 2] = null;
                    refPos[z * 4 + 3] = null;
                }
            }
        }

        removeNonValidateReferencePosition(refPos, symbols, areaLabels);

        // do while it gives reference positions
        for (final ReferencePosition refPo : refPos) {
            this.referencePosition = refPo;
            if (this.referencePosition != null) {
                priorUp.add(this.referencePosition);
                priorDown.add(this.referencePosition);
            }
        }

        while (priorUp.size() != 0) {
            this.referencePosition = priorUp.remove();

            this.label = labels.get(this.referencePosition.nodeNumber);

            resolutionSet.add(new PointTextContainer(this.label.getText(), this.referencePosition.x, this.referencePosition.y, this.label.getPaintFront(), this.label.getPaintBack(), this.label.getSymbol()));

            if (priorUp.size() == 0) {
                return resolutionSet;
            }

            priorUp.remove(refPos[this.referencePosition.nodeNumber * 4 + 0]);
            priorUp.remove(refPos[this.referencePosition.nodeNumber * 4 + 1]);
            priorUp.remove(refPos[this.referencePosition.nodeNumber * 4 + 2]);
            priorUp.remove(refPos[this.referencePosition.nodeNumber * 4 + 3]);

            priorDown.remove(refPos[this.referencePosition.nodeNumber * 4 + 0]);
            priorDown.remove(refPos[this.referencePosition.nodeNumber * 4 + 1]);
            priorDown.remove(refPos[this.referencePosition.nodeNumber * 4 + 2]);
            priorDown.remove(refPos[this.referencePosition.nodeNumber * 4 + 3]);

            final LinkedList<ReferencePosition> linkedRef = new LinkedList<ReferencePosition>();

            while (priorDown.size() != 0) {
                if (priorDown.peek().x < this.referencePosition.x + this.referencePosition.width) {
                    linkedRef.add(priorDown.remove());
                } else {
                    break;
                }
            }
            // brute Force collision test (faster then sweep line for a small
            // amount of
            // objects)
            for (int i = 0; i < linkedRef.size(); i++) {
                if (linkedRef.get(i).x <= this.referencePosition.x + this.referencePosition.width && linkedRef.get(i).y >= this.referencePosition.y - linkedRef.get(i).height && linkedRef.get(i).y <= this.referencePosition.y + linkedRef.get(i).height) {
                    priorUp.remove(linkedRef.get(i));
                    linkedRef.remove(i);
                    i--;
                }
            }
            priorDown.addAll(linkedRef);
        }

        return resolutionSet;
    }

    private void removeEmptySymbolReferences(final List<PointTextContainer> nodes, final List<SymbolContainer> symbols) {
        for (int i = 0; i < nodes.size(); i++) {
            this.label = nodes.get(i);
            if (!symbols.contains(this.label.getSymbol())) {
                this.label.setSymbol(null);
            }
        }
    }

    /**
     * The greedy algorithms need possible label positions, to choose the best
     * among them. This method removes the reference points, that are not
     * validate. Not validate means, that the Reference overlap with another
     * symbol or label or is outside of the object.
     * 
     * @param refPos
     *            list of the potential positions
     * @param symbols
     *            actual list of the symbols
     * @param areaLabels
     *            actual list of the area labels
     */
    private void removeNonValidateReferencePosition(final ReferencePosition[] refPos, final List<SymbolContainer> symbols, final List<PointTextContainer> areaLabels) {
        int distance = LABEL_DISTANCE_TO_SYMBOL;

        for (int i = 0; i < symbols.size(); i++) {
            this.symbolContainer = symbols.get(i);
            this.rect1 = new Rectangle((int) this.symbolContainer.getPoint().getX() - distance, (int) this.symbolContainer.getPoint().getY() - distance, (int) this.symbolContainer.getPoint().getX() + this.symbolContainer.getSymbol().getWidth() + distance, (int) this.symbolContainer.getPoint().getY() + this.symbolContainer.getSymbol().getHeight() + distance);

            for (int y = 0; y < refPos.length; y++) {
                if (refPos[y] != null) {
                    this.rect2 = new Rectangle((int) refPos[y].x, (int) (refPos[y].y - refPos[y].height), (int) (refPos[y].x + refPos[y].width), (int) refPos[y].y);

                    if (this.rect2.intersects(this.rect1)) {
                        refPos[y] = null;
                    }
                }
            }
        }

        distance = LABEL_DISTANCE_TO_LABEL;

        for (final PointTextContainer areaLabel : areaLabels) {
            this.rect1 = new Rectangle((int) areaLabel.getX() - distance, (int) areaLabel.getY() - areaLabel.getBoundary().getHeight() - distance, (int) areaLabel.getX() + areaLabel.getBoundary().getWidth() + distance, (int) areaLabel.getY() + distance);

            for (int y = 0; y < refPos.length; y++) {
                if (refPos[y] != null) {
                    this.rect2 = new Rectangle((int) refPos[y].x, (int) (refPos[y].y - refPos[y].height), (int) (refPos[y].x + refPos[y].width), (int) refPos[y].y);

                    if (this.rect2.intersects(this.rect1)) {
                        refPos[y] = null;
                    }
                }
            }
        }

        this.dependencyCache.removeReferencePointsFromDependencyCache(refPos);
    }

    /**
     * This method removes the area labels, that are not visible in the actual
     * object.
     * 
     * @param areaLabels
     *            area Labels from the actual object
     */
    private void removeOutOfTileAreaLabels(final List<PointTextContainer> areaLabels) {
        for (int i = 0; i < areaLabels.size(); i++) {
            this.label = areaLabels.get(i);

            if (this.label.getX() > Tile.TILE_SIZE) {
                areaLabels.remove(i);

                i--;
            } else if (this.label.getY() - this.label.getBoundary().getHeight() > Tile.TILE_SIZE) {
                areaLabels.remove(i);

                i--;
            } else if (this.label.getX() + this.label.getBoundary().getWidth() < 0.0f) {
                areaLabels.remove(i);

                i--;
            } else if (this.label.getY() + this.label.getBoundary().getHeight() < 0.0f) {
                areaLabels.remove(i);

                i--;
            }
        }
    }

    /**
     * This method removes the labels, that are not visible in the actual
     * object.
     * 
     * @param labels
     *            Labels from the actual object
     */
    private void removeOutOfTileLabels(final List<PointTextContainer> labels) {
        for (int i = 0; i < labels.size();) {
            this.label = labels.get(i);

            if (this.label.getX() - this.label.getBoundary().getWidth() / 2 > Tile.TILE_SIZE) {
                labels.remove(i);
                this.label = null;
            } else if (this.label.getY() - this.label.getBoundary().getHeight() > Tile.TILE_SIZE) {
                labels.remove(i);
                this.label = null;
            } else if (this.label.getX() - this.label.getBoundary().getWidth() / 2 + this.label.getBoundary().getWidth() < 0.0f) {
                labels.remove(i);
                this.label = null;
            } else if (this.label.getY() < 0.0f) {
                labels.remove(i);
                this.label = null;
            } else {
                i++;
            }
        }
    }

    /**
     * This method removes the Symbols, that are not visible in the actual
     * object.
     * 
     * @param symbols
     *            Symbols from the actual object
     */
    private void removeOutOfTileSymbols(final List<SymbolContainer> symbols) {
        for (int i = 0; i < symbols.size();) {
            this.symbolContainer = symbols.get(i);

            if (this.symbolContainer.getPoint().getX() > Tile.TILE_SIZE) {
                symbols.remove(i);
            } else if (this.symbolContainer.getPoint().getY() > Tile.TILE_SIZE) {
                symbols.remove(i);
            } else if (this.symbolContainer.getPoint().getX() + this.symbolContainer.getSymbol().getWidth() < 0.0f) {
                symbols.remove(i);
            } else if (this.symbolContainer.getPoint().getY() + this.symbolContainer.getSymbol().getHeight() < 0.0f) {
                symbols.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * This method removes all the area labels, that overlap each other. So that
     * the output is collision free
     * 
     * @param areaLabels
     *            area labels from the actual object
     */
    private void removeOverlappingAreaLabels(final List<PointTextContainer> areaLabels) {
        final int dis = LABEL_DISTANCE_TO_LABEL;

        for (int x = 0; x < areaLabels.size(); x++) {
            this.label = areaLabels.get(x);
            this.rect1 = new Rectangle((int) this.label.getX() - dis, (int) this.label.getY() - dis, (int) (this.label.getX() + this.label.getBoundary().getWidth()) + dis, (int) (this.label.getY() + this.label.getBoundary().getHeight() + dis));

            for (int y = x + 1; y < areaLabels.size(); y++) {
                if (y != x) {
                    this.label = areaLabels.get(y);
                    this.rect2 = new Rectangle((int) this.label.getX(), (int) this.label.getY(), (int) (this.label.getX() + this.label.getBoundary().getWidth()), (int) (this.label.getY() + this.label.getBoundary().getHeight()));

                    if (this.rect1.intersects(this.rect2)) {
                        areaLabels.remove(y);

                        y--;
                    }
                }
            }
        }
    }

    /**
     * This method removes all the Symbols, that overlap each other. So that the
     * output is collision free.
     * 
     * @param symbols
     *            symbols from the actual object
     */
    public void removeOverlappingSymbols(final List<SymbolContainer> symbols) {
        final int dis = SYMBOL_DISTANCE_TO_SYMBOL;

        for (int x = 0; x < symbols.size(); x++) {
            this.symbolContainer = symbols.get(x);
            this.rect1 = new Rectangle((int) this.symbolContainer.getPoint().getX() - dis, (int) this.symbolContainer.getPoint().getY() - dis, (int) this.symbolContainer.getPoint().getX() + this.symbolContainer.getSymbol().getWidth() + dis, (int) this.symbolContainer.getPoint().getY() + this.symbolContainer.getSymbol().getHeight() + dis);

            for (int y = x + 1; y < symbols.size(); y++) {
                if (y != x) {
                    this.symbolContainer = symbols.get(y);
                    this.rect2 = new Rectangle((int) this.symbolContainer.getPoint().getX(), (int) this.symbolContainer.getPoint().getY(), (int) this.symbolContainer.getPoint().getX() + this.symbolContainer.getSymbol().getWidth(), (int) this.symbolContainer.getPoint().getY() + this.symbolContainer.getSymbol().getHeight());

                    if (this.rect2.intersects(this.rect1)) {
                        symbols.remove(y);
                        y--;
                    }
                }
            }
        }
    }

    /**
     * Removes the the symbols that overlap with area labels.
     * 
     * @param symbols
     *            list of symbols
     * @param pTC
     *            list of labels
     */
    private void removeOverlappingSymbolsWithAreaLabels(final List<SymbolContainer> symbols, final List<PointTextContainer> pTC) {
        final int dis = LABEL_DISTANCE_TO_SYMBOL;

        for (int x = 0; x < pTC.size(); x++) {
            this.label = pTC.get(x);

            this.rect1 = new Rectangle((int) this.label.getX() - dis, (int) (this.label.getY() - this.label.getBoundary().getHeight()) - dis, (int) (this.label.getX() + this.label.getBoundary().getWidth() + dis), (int) (this.label.getY() + dis));

            for (int y = 0; y < symbols.size(); y++) {
                this.symbolContainer = symbols.get(y);

                this.rect2 = new Rectangle((int) this.symbolContainer.getPoint().getX(), (int) this.symbolContainer.getPoint().getY(), (int) (this.symbolContainer.getPoint().getX() + this.symbolContainer.getSymbol().getWidth()), (int) (this.symbolContainer.getPoint().getY() + this.symbolContainer.getSymbol().getHeight()));

                if (this.rect1.intersects(this.rect2)) {
                    symbols.remove(y);
                    y--;
                }
            }
        }
    }
}
