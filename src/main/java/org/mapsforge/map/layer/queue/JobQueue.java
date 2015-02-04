/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.queue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.mapsforge.map.model.MapViewPosition;

public class JobQueue<T extends Job> {
	private static final int QUEUE_CAPACITY = 128;

	private final MapViewPosition mapViewPosition;
	private final List<QueueItem<T>> queueItems = new LinkedList<QueueItem<T>>();
	private boolean scheduleNeeded;

	public JobQueue(final MapViewPosition mapViewPosition) {
		this.mapViewPosition = mapViewPosition;
	}

	public synchronized void add(final T tile) {
		final QueueItem<T> queueItem = new QueueItem<T>(tile);
		if (!this.queueItems.contains(queueItem)) {
			this.queueItems.add(queueItem);
			this.scheduleNeeded = true;
		}
	}

	public synchronized void notifyWorkers() {
		this.notifyAll();
	}

	/**
	 * Returns the most important entry from this queue. The method blocks while
	 * this queue is empty.
	 */
	public synchronized T remove() throws InterruptedException {
		while (this.queueItems.isEmpty()) {
			this.wait();
		}

		if (this.scheduleNeeded) {
			this.scheduleNeeded = false;
			schedule();
		}

		return this.queueItems.remove(0).getObject();
	}

	private void schedule() {
		QueueItemScheduler.schedule(this.queueItems, this.mapViewPosition.getMapPosition());
		Collections.sort(this.queueItems, QueueItemComparator.getInstance());
		trimToSize();
	}

	/**
	 * @return the current number of entries in this queue.
	 */
	public synchronized int size() {
		return this.queueItems.size();
	}

	private void trimToSize() {
		int queueSize = this.queueItems.size();

		while (queueSize > QUEUE_CAPACITY) {
			this.queueItems.remove(--queueSize);
		}
	}
}
