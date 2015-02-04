/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing.view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		super("MapViewer");

		setSize(new Dimension(800, 600));
		getContentPane().setLayout(new CardLayout(0, 0));

	}

}
