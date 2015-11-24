/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.core.util;

import it.unibo.alchemist.utils.L;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;

/**
 * A utility class with IO-specific helper methods.
 * 
 */
public final class IOUtils {

    /**
     * Invokes the {@link Closeable#close()} method on the given object. If an
     * {@link IOException} occurs during the method call, it will be caught and
     * logged on level {@link Level#WARNING}.
     * 
     * @param closeable
     *            the data source which should be closed (may be null).
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            L.warn(e);
        }
    }

    private IOUtils() {
        throw new IllegalStateException();
    }
}
