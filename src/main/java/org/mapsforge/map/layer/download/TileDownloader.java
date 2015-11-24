/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.util.IOUtils;

public class TileDownloader {
    private static final int TIMEOUT_CONNECT = 5000;
    private static final int TIMEOUT_READ = 10000;

    private final DownloadJob downloadJob;

    private final GraphicFactory graphicFactory;

    private static InputStream getInputStream(final URLConnection urlConnection) throws IOException {
        if ("gzip".equals(urlConnection.getContentEncoding())) {
            return new GZIPInputStream(urlConnection.getInputStream());
        }
        return urlConnection.getInputStream();
    }

    private static URLConnection getURLConnection(final URL url) throws IOException {
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        urlConnection.setReadTimeout(TIMEOUT_READ);
        return urlConnection;
    }

    public TileDownloader(final DownloadJob downloadJob, final GraphicFactory graphicFactory) {
        if (downloadJob == null) {
            throw new IllegalArgumentException("downloadJob must not be null");
        } else if (graphicFactory == null) {
            throw new IllegalArgumentException("graphicFactory must not be null");
        }

        this.downloadJob = downloadJob;
        this.graphicFactory = graphicFactory;
    }

    public Bitmap downloadImage() throws IOException {
        final URL url = this.downloadJob.getTileSource().getTileUrl(this.downloadJob.getTile());
        final URLConnection urlConnection = getURLConnection(url);
        final InputStream inputStream = getInputStream(urlConnection);

        try {
            return this.graphicFactory.createBitmap(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
