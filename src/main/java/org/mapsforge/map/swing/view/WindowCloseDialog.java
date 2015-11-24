/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.swing.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;

public class WindowCloseDialog extends WindowAdapter {
    private static final String MESSAGE = "Are you sure you want to exit the application?";
    private static final String TITLE = "Confirm close";

    private final JFrame jFrame;
    private final Model model;
    private final PreferencesFacade preferencesFacade;

    public WindowCloseDialog(final JFrame jFrame, final Model model, final PreferencesFacade preferencesFacade) {
        super();

        this.jFrame = jFrame;
        this.model = model;
        this.preferencesFacade = preferencesFacade;

        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        final int result = JOptionPane.showConfirmDialog(this.jFrame, MESSAGE, TITLE, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            this.model.save(this.preferencesFacade);
            this.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }
}
