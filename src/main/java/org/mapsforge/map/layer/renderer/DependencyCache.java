/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.renderer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.model.Tile;

/**
 * This class process the methods for the Dependency Cache. It's connected with
 * the LabelPlacement class. The main goal is, to remove double labels and
 * symbols that are already rendered, from the actual object. Labels and symbols
 * that, would be rendered on an already drawn Tile, will be deleted too.
 */
public class DependencyCache {
	private DependencyOnTile currentDependencyOnTile;

	private Tile currentTile;

	/**
	 * Hash table, that connects the Tiles with their entries in the dependency
	 * cache.
	 */
	private final Map<Tile, DependencyOnTile> dependencyTable;

	private Dependency<DependencyText> depLabel;

	private Rectangle rect1;
	private Rectangle rect2;

	private SymbolContainer smb;
	private DependencyOnTile tmp;

	/**
	 * The class holds the data for a symbol with dependencies on other tiles.
	 * 
	 * @param <Type>
	 *            only two types are reasonable. The DependencySymbol or
	 *            DependencyText class.
	 */
	private static class Dependency<Type> {
		final Point point;
		final Type value;

		Dependency(final Type value, final Point point) {
			this.value = value;
			this.point = point;
		}
	}

	/**
	 * This class holds all the information off the possible dependencies on a
	 * object.
	 */
	private static class DependencyOnTile {
		boolean drawn;
		List<Dependency<DependencyText>> labels;
		List<Dependency<DependencySymbol>> symbols;

		/**
		 * Initialize label, symbol and drawn.
		 */
		public DependencyOnTile() {
			this.labels = null;
			this.symbols = null;
			this.drawn = false;
		}

		/**
		 * @param toAdd
		 *            a dependency Symbol
		 */
		public void addSymbol(final Dependency<DependencySymbol> toAdd) {
			if (this.symbols == null) {
				this.symbols = new ArrayList<Dependency<DependencySymbol>>();
			}
			this.symbols.add(toAdd);
		}

		/**
		 * @param toAdd
		 *            a Dependency Text
		 */
		public void addText(final Dependency<DependencyText> toAdd) {
			if (this.labels == null) {
				this.labels = new ArrayList<Dependency<DependencyText>>();
			}
			this.labels.add(toAdd);
		}
	}

	/**
	 * The class holds the data for a symbol with dependencies on other tiles.
	 */
	private static class DependencySymbol {
		private final List<Tile> tiles;
		final Bitmap symbol;

		/**
		 * Creates a symbol dependency element for the dependency cache.
		 * 
		 * @param symbol
		 *            reference on the dependency symbol.
		 */
		public DependencySymbol(final Bitmap symbol, final Tile tile) {
			this.symbol = symbol;
			this.tiles = new LinkedList<Tile>();
			this.tiles.add(tile);
		}

		/**
		 * Adds an additional object, which has an dependency with this symbol.
		 */
		public void addTile(final Tile tile) {
			this.tiles.add(tile);
		}
	}

	/**
	 * The class holds the data for a label with dependencies on other tiles.
	 */
	private static class DependencyText {
		final Rectangle boundary;
		final Paint paintBack;
		final Paint paintFront;
		final String text;
		final List<Tile> tiles;

		/**
		 * Creates a text dependency in the dependency cache.
		 * 
		 * @param paintFront
		 *            paint element from the front.
		 * @param paintBack
		 *            paint element form the background of the text.
		 * @param text
		 *            the text of the element.
		 * @param boundary
		 *            the fixed boundary with width and height.
		 */
		public DependencyText(final Paint paintFront, final Paint paintBack, final String text, final Rectangle boundary, final Tile tile) {
			this.paintFront = paintFront;
			this.paintBack = paintBack;
			this.text = text;
			this.tiles = new LinkedList<Tile>();
			this.tiles.add(tile);
			this.boundary = boundary;
		}

		public void addTile(final Tile tile) {
			this.tiles.add(tile);
		}
	}

	/**
	 * Constructor for this class, that creates a hashtable for the
	 * dependencies.
	 */
	public DependencyCache() {
		this.dependencyTable = new Hashtable<Tile, DependencyOnTile>(60);
	}

	private void addLabelsFromDependencyOnTile(final List<PointTextContainer> labels) {
		for (int i = 0; i < this.currentDependencyOnTile.labels.size(); i++) {
			this.depLabel = this.currentDependencyOnTile.labels.get(i);
			if (this.depLabel.value.paintBack != null) {
				labels.add(new PointTextContainer(this.depLabel.value.text, this.depLabel.point.getX(), this.depLabel.point.getY(), this.depLabel.value.paintFront, this.depLabel.value.paintBack));
			} else {
				labels.add(new PointTextContainer(this.depLabel.value.text, this.depLabel.point.getX(), this.depLabel.point.getY(), this.depLabel.value.paintFront));
			}
		}
	}

	private void addSymbolsFromDependencyOnTile(final List<SymbolContainer> symbols) {
		for (final Dependency<DependencySymbol> depSmb : this.currentDependencyOnTile.symbols) {
			symbols.add(new SymbolContainer(depSmb.value.symbol, depSmb.point));
		}
	}

	/**
	 * Fills the dependency entry from the object and the neighbor tiles with
	 * the dependency information, that are necessary for drawing. To do that
	 * every label and symbol that will be drawn, will be checked if it produces
	 * dependencies with other tiles.
	 * 
	 * @param pTC
	 *            list of the labels
	 */
	private void fillDependencyLabels(final List<PointTextContainer> pTC) {
		final Tile left = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile right = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile up = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile down = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		final Tile leftup = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile leftdown = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());
		final Tile rightup = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile rightdown = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		PointTextContainer label;
		DependencyOnTile linkedDep;
		DependencyText toAdd;

		for (int i = 0; i < pTC.size(); i++) {
			label = pTC.get(i);

			toAdd = null;

			// up
			if (label.getY() - label.getBoundary().getHeight() < 0.0f && !this.dependencyTable.get(up).drawn) {
				linkedDep = this.dependencyTable.get(up);

				toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

				this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));

				linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY() + Tile.TILE_SIZE)));

				toAdd.addTile(up);

				if (label.getX() < 0.0f && !this.dependencyTable.get(leftup).drawn) {
					linkedDep = this.dependencyTable.get(leftup);

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY() + Tile.TILE_SIZE)));

					toAdd.addTile(leftup);
				}

				if (label.getX() + label.getBoundary().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightup).drawn) {
					linkedDep = this.dependencyTable.get(rightup);

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY() + Tile.TILE_SIZE)));

					toAdd.addTile(rightup);
				}
			}

			// down
			if (label.getY() > Tile.TILE_SIZE && !this.dependencyTable.get(down).drawn) {
				linkedDep = this.dependencyTable.get(down);

				if (toAdd == null) {
					toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

					this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
				}

				linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY() - Tile.TILE_SIZE)));

				toAdd.addTile(down);

				if (label.getX() < 0.0f && !this.dependencyTable.get(leftdown).drawn) {
					linkedDep = this.dependencyTable.get(leftdown);

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY() - Tile.TILE_SIZE)));

					toAdd.addTile(leftdown);
				}

				if (label.getX() + label.getBoundary().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightdown).drawn) {
					linkedDep = this.dependencyTable.get(rightdown);

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY() - Tile.TILE_SIZE)));

					toAdd.addTile(rightdown);
				}
			}
			// left

			if (label.getX() < 0.0f && !this.dependencyTable.get(left).drawn) {
				linkedDep = this.dependencyTable.get(left);

				if (toAdd == null) {
					toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

					this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
				}

				linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY())));

				toAdd.addTile(left);
			}
			// right
			if (label.getX() + label.getBoundary().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(right).drawn) {
				linkedDep = this.dependencyTable.get(right);

				if (toAdd == null) {
					toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

					this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
				}

				linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY())));

				toAdd.addTile(right);
			}

			// check symbols

			if (label.getSymbol() != null && toAdd == null) {
				if (label.getSymbol().getPoint().getY() <= 0.0f && !this.dependencyTable.get(up).drawn) {
					linkedDep = this.dependencyTable.get(up);

					toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

					this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY() + Tile.TILE_SIZE)));

					toAdd.addTile(up);

					if (label.getSymbol().getPoint().getX() < 0.0f && !this.dependencyTable.get(leftup).drawn) {
						linkedDep = this.dependencyTable.get(leftup);

						linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY() + Tile.TILE_SIZE)));

						toAdd.addTile(leftup);
					}

					if (label.getSymbol().getPoint().getX() + label.getSymbol().getSymbol().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightup).drawn) {
						linkedDep = this.dependencyTable.get(rightup);

						linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY() + Tile.TILE_SIZE)));

						toAdd.addTile(rightup);
					}
				}

				if (label.getSymbol().getPoint().getY() + label.getSymbol().getSymbol().getHeight() >= Tile.TILE_SIZE && !this.dependencyTable.get(down).drawn) {
					linkedDep = this.dependencyTable.get(down);

					if (toAdd == null) {
						toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

						this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
					}

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY() + Tile.TILE_SIZE)));

					toAdd.addTile(up);

					if (label.getSymbol().getPoint().getX() < 0.0f && !this.dependencyTable.get(leftdown).drawn) {
						linkedDep = this.dependencyTable.get(leftdown);

						linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY() - Tile.TILE_SIZE)));

						toAdd.addTile(leftdown);
					}

					if (label.getSymbol().getPoint().getX() + label.getSymbol().getSymbol().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightdown).drawn) {
						linkedDep = this.dependencyTable.get(rightdown);

						linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY() - Tile.TILE_SIZE)));

						toAdd.addTile(rightdown);
					}
				}

				if (label.getSymbol().getPoint().getX() <= 0.0f && !this.dependencyTable.get(left).drawn) {
					linkedDep = this.dependencyTable.get(left);

					if (toAdd == null) {
						toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

						this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
					}

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() - Tile.TILE_SIZE, label.getY())));

					toAdd.addTile(left);
				}

				if (label.getSymbol().getPoint().getX() + label.getSymbol().getSymbol().getWidth() >= Tile.TILE_SIZE && !this.dependencyTable.get(right).drawn) {
					linkedDep = this.dependencyTable.get(right);

					if (toAdd == null) {
						toAdd = new DependencyText(label.getPaintFront(), label.getPaintBack(), label.getText(), label.getBoundary(), this.currentTile);

						this.currentDependencyOnTile.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX(), label.getY())));
					}

					linkedDep.addText(new Dependency<DependencyText>(toAdd, new Point(label.getX() + Tile.TILE_SIZE, label.getY())));

					toAdd.addTile(right);
				}
			}
		}
	}

	/**
	 * This method fills the entries in the dependency cache of the tiles, if
	 * their dependencies.
	 * 
	 * @param labels
	 *            current labels, that will be displayed.
	 * @param symbols
	 *            current symbols, that will be displayed.
	 * @param areaLabels
	 *            current areaLabels, that will be displayed.
	 */
	public void fillDependencyOnTile(final List<PointTextContainer> labels, final List<SymbolContainer> symbols, final List<PointTextContainer> areaLabels) {
		this.currentDependencyOnTile.drawn = true;

		if (!labels.isEmpty() || !symbols.isEmpty() || !areaLabels.isEmpty()) {
			fillDependencyOnTile2(labels, symbols, areaLabels);
		}

		if (this.currentDependencyOnTile.labels != null) {
			addLabelsFromDependencyOnTile(labels);
		}
		if (this.currentDependencyOnTile.symbols != null) {
			addSymbolsFromDependencyOnTile(symbols);
		}
	}

	private void fillDependencyOnTile2(final List<PointTextContainer> labels, final List<SymbolContainer> symbols, final List<PointTextContainer> areaLabels) {
		final Tile left = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile right = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile up = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile down = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		final Tile leftup = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile leftdown = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());
		final Tile rightup = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile rightdown = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		if (this.dependencyTable.get(up) == null) {
			this.dependencyTable.put(up, new DependencyOnTile());
		}
		if (this.dependencyTable.get(down) == null) {
			this.dependencyTable.put(down, new DependencyOnTile());
		}
		if (this.dependencyTable.get(left) == null) {
			this.dependencyTable.put(left, new DependencyOnTile());
		}
		if (this.dependencyTable.get(right) == null) {
			this.dependencyTable.put(right, new DependencyOnTile());
		}
		if (this.dependencyTable.get(leftdown) == null) {
			this.dependencyTable.put(leftdown, new DependencyOnTile());
		}
		if (this.dependencyTable.get(rightup) == null) {
			this.dependencyTable.put(rightup, new DependencyOnTile());
		}
		if (this.dependencyTable.get(leftup) == null) {
			this.dependencyTable.put(leftup, new DependencyOnTile());
		}
		if (this.dependencyTable.get(rightdown) == null) {
			this.dependencyTable.put(rightdown, new DependencyOnTile());
		}

		fillDependencyLabels(labels);
		fillDependencyLabels(areaLabels);

		DependencyOnTile linkedDep;
		DependencySymbol addSmb;

		for (final SymbolContainer symbol : symbols) {
			addSmb = null;

			// up
			if (symbol.getPoint().getY() < 0.0f && !this.dependencyTable.get(up).drawn) {
				linkedDep = this.dependencyTable.get(up);

				addSmb = new DependencySymbol(symbol.getSymbol(), this.currentTile);
				this.currentDependencyOnTile.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getY())));

				linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getY() + Tile.TILE_SIZE)));
				addSmb.addTile(up);

				if (symbol.getPoint().getX() < 0.0f && !this.dependencyTable.get(leftup).drawn) {
					linkedDep = this.dependencyTable.get(leftup);

					linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() + Tile.TILE_SIZE, symbol.getPoint().getY() + Tile.TILE_SIZE)));
					addSmb.addTile(leftup);
				}

				if (symbol.getPoint().getX() + symbol.getSymbol().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightup).drawn) {
					linkedDep = this.dependencyTable.get(rightup);

					linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() - Tile.TILE_SIZE, symbol.getPoint().getY() + Tile.TILE_SIZE)));
					addSmb.addTile(rightup);
				}
			}

			// down
			if (symbol.getPoint().getY() + symbol.getSymbol().getHeight() > Tile.TILE_SIZE && !this.dependencyTable.get(down).drawn) {
				linkedDep = this.dependencyTable.get(down);

				if (addSmb == null) {
					addSmb = new DependencySymbol(symbol.getSymbol(), this.currentTile);
					this.currentDependencyOnTile.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getY())));
				}

				linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getY() - Tile.TILE_SIZE)));
				addSmb.addTile(down);

				if (symbol.getPoint().getX() < 0.0f && !this.dependencyTable.get(leftdown).drawn) {
					linkedDep = this.dependencyTable.get(leftdown);

					linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() + Tile.TILE_SIZE, symbol.getPoint().getY() - Tile.TILE_SIZE)));
					addSmb.addTile(leftdown);
				}

				if (symbol.getPoint().getX() + symbol.getSymbol().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(rightdown).drawn) {
					linkedDep = this.dependencyTable.get(rightdown);

					linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() - Tile.TILE_SIZE, symbol.getPoint().getY() - Tile.TILE_SIZE)));
					addSmb.addTile(rightdown);
				}
			}

			// left
			if (symbol.getPoint().getX() < 0.0f && !this.dependencyTable.get(left).drawn) {
				linkedDep = this.dependencyTable.get(left);

				if (addSmb == null) {
					addSmb = new DependencySymbol(symbol.getSymbol(), this.currentTile);
					this.currentDependencyOnTile.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getY())));
				}

				linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() + Tile.TILE_SIZE, symbol.getPoint().getY())));
				addSmb.addTile(left);
			}

			// right
			if (symbol.getPoint().getX() + symbol.getSymbol().getWidth() > Tile.TILE_SIZE && !this.dependencyTable.get(right).drawn) {
				linkedDep = this.dependencyTable.get(right);
				if (addSmb == null) {
					addSmb = new DependencySymbol(symbol.getSymbol(), this.currentTile);
					this.currentDependencyOnTile.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX(), symbol.getPoint().getX())));
				}

				linkedDep.addSymbol(new Dependency<DependencySymbol>(addSmb, new Point(symbol.getPoint().getX() - Tile.TILE_SIZE, symbol.getPoint().getY())));
				addSmb.addTile(right);
			}
		}
	}

	/**
	 * This method must be called, before the dependencies will be handled
	 * correctly. Because it sets the actual Tile and looks if it has already
	 * dependencies.
	 */
	public void generateTileAndDependencyOnTile(final Tile tile) {
		this.currentTile = new Tile(tile.getTileX(), tile.getTileY(), tile.getZoomLevel());
		this.currentDependencyOnTile = this.dependencyTable.get(this.currentTile);

		if (this.currentDependencyOnTile == null) {
			this.dependencyTable.put(this.currentTile, new DependencyOnTile());
			this.currentDependencyOnTile = this.dependencyTable.get(this.currentTile);
		}
	}

	/**
	 * Removes the are labels from the actual list, that would be rendered in a
	 * Tile that has already be drawn.
	 * 
	 * @param areaLabels
	 *            current area Labels, that will be displayed
	 */
	public void removeAreaLabelsInAlreadyDrawnAreas(final List<PointTextContainer> areaLabels) {
		final Tile lefttmp = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile righttmp = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile uptmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile downtmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		boolean up;
		boolean left;
		boolean right;
		boolean down;

		this.tmp = this.dependencyTable.get(lefttmp);
		left = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(righttmp);
		right = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(uptmp);
		up = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(downtmp);
		down = this.tmp == null ? false : this.tmp.drawn;

		PointTextContainer label;

		for (int i = 0; i < areaLabels.size(); i++) {
			label = areaLabels.get(i);

			if (up && label.getY() - label.getBoundary().getHeight() < 0.0f) {
				areaLabels.remove(i);
				i--;
				continue;
			}

			if (down && label.getY() > Tile.TILE_SIZE) {
				areaLabels.remove(i);
				i--;
				continue;
			}
			if (left && label.getX() < 0.0f) {
				areaLabels.remove(i);
				i--;
				continue;
			}
			if (right && label.getX() + label.getBoundary().getWidth() > Tile.TILE_SIZE) {
				areaLabels.remove(i);
				i--;
				continue;
			}
		}
	}

	private void removeOverlappingAreaLabelsWithDependencyLabels(final List<PointTextContainer> areaLabels) {
		PointTextContainer pTC;

		for (int i = 0; i < this.currentDependencyOnTile.labels.size(); i++) {
			this.depLabel = this.currentDependencyOnTile.labels.get(i);
			this.rect1 = new Rectangle((int) this.depLabel.point.getX(), (int) (this.depLabel.point.getY() - this.depLabel.value.boundary.getHeight()), (int) (this.depLabel.point.getX() + this.depLabel.value.boundary.getWidth()), (int) this.depLabel.point.getY());

			for (int x = 0; x < areaLabels.size(); x++) {
				pTC = areaLabels.get(x);

				this.rect2 = new Rectangle((int) pTC.getX(), (int) pTC.getY() - pTC.getBoundary().getHeight(), (int) pTC.getX() + pTC.getBoundary().getWidth(), (int) pTC.getY());

				if (this.rect2.intersects(this.rect1)) {
					areaLabels.remove(x);
					x--;
				}
			}
		}
	}

	private void removeOverlappingAreaLabelsWithDependencySymbols(final List<PointTextContainer> areaLabels) {
		PointTextContainer label;

		for (final Dependency<DependencySymbol> depSmb : this.currentDependencyOnTile.symbols) {
			this.rect1 = new Rectangle((int) depSmb.point.getX(), (int) depSmb.point.getY(), (int) depSmb.point.getX() + depSmb.value.symbol.getWidth(), (int) depSmb.point.getY() + depSmb.value.symbol.getHeight());

			for (int x = 0; x < areaLabels.size(); x++) {
				label = areaLabels.get(x);

				this.rect2 = new Rectangle((int) label.getX(), (int) (label.getY() - label.getBoundary().getHeight()), (int) (label.getX() + label.getBoundary().getWidth()), (int) label.getY());

				if (this.rect2.intersects(this.rect1)) {
					areaLabels.remove(x);
					x--;
				}
			}
		}
	}

	private void removeOverlappingLabelsWithDependencyLabels(final List<PointTextContainer> labels) {
		for (int i = 0; i < this.currentDependencyOnTile.labels.size(); i++) {
			for (int x = 0; x < labels.size(); x++) {
				if (labels.get(x).getText().equals(this.currentDependencyOnTile.labels.get(i).value.text) && labels.get(x).getPaintFront().equals(this.currentDependencyOnTile.labels.get(i).value.paintFront) && labels.get(x).getPaintBack().equals(this.currentDependencyOnTile.labels.get(i).value.paintBack)) {
					labels.remove(x);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * Removes all objects that overlaps with the objects from the dependency
	 * cache.
	 * 
	 * @param labels
	 *            labels from the current object
	 * @param areaLabels
	 *            area labels from the current object
	 * @param symbols
	 *            symbols from the current object
	 */
	public void removeOverlappingObjectsWithDependencyOnTile(final List<PointTextContainer> labels, final List<PointTextContainer> areaLabels, final List<SymbolContainer> symbols) {
		if (this.currentDependencyOnTile.labels != null && this.currentDependencyOnTile.labels.size() != 0) {
			removeOverlappingLabelsWithDependencyLabels(labels);
			removeOverlappingSymbolsWithDependencyLabels(symbols);
			removeOverlappingAreaLabelsWithDependencyLabels(areaLabels);
		}

		if (this.currentDependencyOnTile.symbols != null && this.currentDependencyOnTile.symbols.size() != 0) {
			removeOverlappingSymbolsWithDepencySymbols(symbols, 2);
			removeOverlappingAreaLabelsWithDependencySymbols(areaLabels);
		}
	}

	private void removeOverlappingSymbolsWithDepencySymbols(final List<SymbolContainer> symbols, final int dis) {
		SymbolContainer symbolContainer;
		Dependency<DependencySymbol> sym2;

		for (int x = 0; x < this.currentDependencyOnTile.symbols.size(); x++) {
			sym2 = this.currentDependencyOnTile.symbols.get(x);
			this.rect1 = new Rectangle((int) sym2.point.getX() - dis, (int) sym2.point.getY() - dis, (int) sym2.point.getX() + sym2.value.symbol.getWidth() + dis, (int) sym2.point.getY() + sym2.value.symbol.getHeight() + dis);

			for (int y = 0; y < symbols.size(); y++) {
				symbolContainer = symbols.get(y);
				this.rect2 = new Rectangle((int) symbolContainer.getPoint().getX(), (int) symbolContainer.getPoint().getY(), (int) symbolContainer.getPoint().getX() + symbolContainer.getSymbol().getWidth(), (int) symbolContainer.getPoint().getY() + symbolContainer.getSymbol().getHeight());

				if (this.rect2.intersects(this.rect1)) {
					symbols.remove(y);
					y--;
				}
			}
		}
	}

	private void removeOverlappingSymbolsWithDependencyLabels(final List<SymbolContainer> symbols) {
		for (int i = 0; i < this.currentDependencyOnTile.labels.size(); i++) {
			this.depLabel = this.currentDependencyOnTile.labels.get(i);
			this.rect1 = new Rectangle((int) this.depLabel.point.getX(), (int) (this.depLabel.point.getY() - this.depLabel.value.boundary.getHeight()), (int) (this.depLabel.point.getX() + this.depLabel.value.boundary.getWidth()), (int) this.depLabel.point.getY());

			for (int x = 0; x < symbols.size(); x++) {
				this.smb = symbols.get(x);

				this.rect2 = new Rectangle((int) this.smb.getPoint().getX(), (int) this.smb.getPoint().getY(), (int) this.smb.getPoint().getX() + this.smb.getSymbol().getWidth(), (int) this.smb.getPoint().getY() + this.smb.getSymbol().getHeight());

				if (this.rect2.intersects(this.rect1)) {
					symbols.remove(x);
					x--;
				}
			}
		}
	}

	/**
	 * When the LabelPlacement class generates potential label positions for an
	 * POI, there should be no possible positions, that collide with existing
	 * symbols or labels in the dependency Cache. This class implements this
	 * functionality.
	 * 
	 * @param refPos
	 *            possible label positions form the two or four point Greedy
	 */
	public void removeReferencePointsFromDependencyCache(final LabelPlacement.ReferencePosition[] refPos) {
		final Tile lefttmp = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile righttmp = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		final Tile uptmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		final Tile downtmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());

		boolean up;
		boolean left;
		boolean right;
		boolean down;

		this.tmp = this.dependencyTable.get(lefttmp);
		left = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(righttmp);
		right = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(uptmp);
		up = this.tmp == null ? false : this.tmp.drawn;

		this.tmp = this.dependencyTable.get(downtmp);
		down = this.tmp == null ? false : this.tmp.drawn;

		LabelPlacement.ReferencePosition ref;

		for (int i = 0; i < refPos.length; i++) {
			ref = refPos[i];

			if (ref == null) {
				continue;
			}

			if (up && ref.y - ref.height < 0) {
				refPos[i] = null;
				continue;
			}

			if (down && ref.y >= Tile.TILE_SIZE) {
				refPos[i] = null;
				continue;
			}

			if (left && ref.x < 0) {
				refPos[i] = null;
				continue;
			}

			if (right && ref.x + ref.width > Tile.TILE_SIZE) {
				refPos[i] = null;
			}
		}

		// removes all Reverence Points that intersects with Labels from the
		// Dependency Cache

		final int dis = 2;
		if (this.currentDependencyOnTile != null) {
			if (this.currentDependencyOnTile.labels != null) {
				for (int i = 0; i < this.currentDependencyOnTile.labels.size(); i++) {
					this.depLabel = this.currentDependencyOnTile.labels.get(i);
					this.rect1 = new Rectangle((int) this.depLabel.point.getX() - dis, (int) (this.depLabel.point.getY() - this.depLabel.value.boundary.getHeight()) - dis, (int) (this.depLabel.point.getX() + this.depLabel.value.boundary.getWidth() + dis), (int) (this.depLabel.point.getY() + dis));

					for (int y = 0; y < refPos.length; y++) {
						if (refPos[y] != null) {
							this.rect2 = new Rectangle((int) refPos[y].x, (int) (refPos[y].y - refPos[y].height), (int) (refPos[y].x + refPos[y].width), (int) refPos[y].y);

							if (this.rect2.intersects(this.rect1)) {
								refPos[y] = null;
							}
						}
					}
				}
			}
			if (this.currentDependencyOnTile.symbols != null) {
				for (final Dependency<DependencySymbol> symbols2 : this.currentDependencyOnTile.symbols) {
					this.rect1 = new Rectangle((int) symbols2.point.getX(), (int) symbols2.point.getY(), (int) (symbols2.point.getX() + symbols2.value.symbol.getWidth()), (int) (symbols2.point.getY() + symbols2.value.symbol.getHeight()));

					for (int y = 0; y < refPos.length; y++) {
						if (refPos[y] != null) {
							this.rect2 = new Rectangle((int) refPos[y].x, (int) (refPos[y].y - refPos[y].height), (int) (refPos[y].x + refPos[y].width), (int) refPos[y].y);

							if (this.rect2.intersects(this.rect1)) {
								refPos[y] = null;
							}
						}
					}
				}
			}
		}
	}

	public void removeSymbolsFromDrawnAreas(final List<SymbolContainer> symbols) {
		final double maxTileNumber = Math.pow(2, this.currentTile.getZoomLevel()) - 1;

		Tile lefttmp = null;
		if (this.currentTile.getTileX() > 0) {
			lefttmp = new Tile(this.currentTile.getTileX() - 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		}

		Tile righttmp = null;
		if (this.currentTile.getTileX() < maxTileNumber) {
			righttmp = new Tile(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), this.currentTile.getZoomLevel());
		}

		Tile uptmp = null;
		if (this.currentTile.getTileY() > 0) {
			uptmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() - 1, this.currentTile.getZoomLevel());
		}

		Tile downtmp = null;
		if (this.currentTile.getTileY() < maxTileNumber) {
			downtmp = new Tile(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, this.currentTile.getZoomLevel());
		}

		boolean up;
		boolean left;
		boolean right;
		boolean down;

		if (lefttmp == null) {
			this.tmp = null;
		} else {
			this.tmp = this.dependencyTable.get(lefttmp);
		}

		left = this.tmp == null ? false : this.tmp.drawn;

		if (righttmp == null) {
			this.tmp = null;
		} else {
			this.tmp = this.dependencyTable.get(righttmp);
		}

		right = this.tmp == null ? false : this.tmp.drawn;

		if (uptmp == null) {
			this.tmp = null;
		} else {
			this.tmp = this.dependencyTable.get(uptmp);
		}
		up = this.tmp == null ? false : this.tmp.drawn;

		if (downtmp == null) {
			this.tmp = null;
		} else {
			this.tmp = this.dependencyTable.get(downtmp);
		}
		down = this.tmp == null ? false : this.tmp.drawn;

		SymbolContainer ref;

		for (int i = 0; i < symbols.size(); i++) {
			ref = symbols.get(i);

			if (up && ref.getPoint().getX() < 0) {
				symbols.remove(i);
				i--;
				continue;
			}

			if (down && ref.getPoint().getY() + ref.getSymbol().getHeight() > Tile.TILE_SIZE) {
				symbols.remove(i);
				i--;
				continue;
			}
			if (left && ref.getPoint().getX() < 0) {
				symbols.remove(i);
				i--;
				continue;
			}
			if (right && ref.getPoint().getX() + ref.getSymbol().getWidth() > Tile.TILE_SIZE) {
				symbols.remove(i);
				i--;
				continue;
			}
		}
	}
}
