/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.view;

import java.util.concurrent.TimeUnit;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;

/**
 * An FPS counter measures the drawing frame rate.
 */
public class FpsCounter {
	private static final long ONE_SECOND = TimeUnit.SECONDS.toNanos(1);

	private String fps;

	private int frameCounter;
	private long lastTime;
	private final Paint paint;
	private boolean visible;

	private static Paint createPaint(final GraphicFactory graphicFactory) {
		final Paint paint = graphicFactory.createPaint();
		paint.setColor(graphicFactory.createColor(Color.BLACK));
		paint.setStrokeWidth(0);
		paint.setStyle(Style.STROKE);
		paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
		paint.setTextSize(25);
		return paint;
	}

	public FpsCounter(final GraphicFactory graphicFactory) {
		this.paint = createPaint(graphicFactory);
	}

	public void draw(final Canvas canvas) {
		if (!this.visible) {
			return;
		}

		final long currentTime = System.nanoTime();
		final long elapsedTime = currentTime - this.lastTime;
		if (elapsedTime > ONE_SECOND) {
			this.fps = String.valueOf(Math.round(this.frameCounter * ONE_SECOND / elapsedTime));
			this.lastTime = currentTime;
			this.frameCounter = 0;
		}

		canvas.drawText(this.fps, 20, 40, this.paint);
		++this.frameCounter;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}
}
