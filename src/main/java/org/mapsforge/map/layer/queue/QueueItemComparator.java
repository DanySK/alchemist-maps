/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.queue;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

final class QueueItemComparator implements Comparator<QueueItem<?>>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final QueueItemComparator INSTANCE = new QueueItemComparator();

	public static QueueItemComparator getInstance() {
		return INSTANCE;
	}

	private QueueItemComparator() {
		// do nothing
	}

	@Override
	public int compare(final QueueItem<?> queueItem1, final QueueItem<?> queueItem2) {
		if (queueItem1.getPriority() < queueItem2.getPriority()) {
			return -1;
		} else if (queueItem1.getPriority() > queueItem2.getPriority()) {
			return 1;
		}
		return 0;
	}

	@Override
	public Comparator<QueueItem<?>> reversed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Comparator<QueueItem<?>> thenComparing(final Comparator<? super QueueItem<?>> other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> Comparator<QueueItem<?>> thenComparing(final Function<? super QueueItem<?>, ? extends U> keyExtractor, final Comparator<? super U> keyComparator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<QueueItem<?>> thenComparing(final Function<? super QueueItem<?>, ? extends U> keyExtractor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Comparator<QueueItem<?>> thenComparingInt(final ToIntFunction<? super QueueItem<?>> keyExtractor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Comparator<QueueItem<?>> thenComparingLong(final ToLongFunction<? super QueueItem<?>> keyExtractor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Comparator<QueueItem<?>> thenComparingDouble(final ToDoubleFunction<? super QueueItem<?>> keyExtractor) {
		throw new UnsupportedOperationException();
	}

}
