/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map;

import it.unibo.alchemist.utils.L;

/**
 * An abstract base class for threads which support pausing and resuming.
 * 
 * @author Mapsforge
 * @author Giovanni Ciatto
 * @author Danilo Pianini
 *
 */
public abstract class PausableThread extends Thread {

	private boolean pausing;
	private boolean shouldPause;

	/**
	 * Specifies the scheduling priority of a {@link Thread}.
	 */
	protected enum ThreadPriority {
		/**
		 * The priority between {@link #NORMAL} and {@link #HIGHEST}.
		 */
		ABOVE_NORMAL((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2),

		/**
		 * The priority between {@link #LOWEST} and {@link #NORMAL}.
		 */
		BELOW_NORMAL((Thread.NORM_PRIORITY + Thread.MIN_PRIORITY) / 2),

		/**
		 * The maximum priority a thread can have.
		 */
		HIGHEST(MAX_PRIORITY),

		/**
		 * The minimum priority a thread can have.
		 */
		LOWEST(MIN_PRIORITY),

		/**
		 * The default priority of a thread.
		 */
		NORMAL(NORM_PRIORITY);

		private final int prio;

		private ThreadPriority(final int priority) {
			if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
				throw new IllegalArgumentException("invalid priority: " + priority);
			}
			this.prio = priority;
		}
	}

	/**
	 * Called once at the end of the {@link #run()} method. The default
	 * implementation is empty.
	 */
	protected void afterRun() {
		// do nothing
	}

	/**
	 * Causes the current thread to wait until this thread is pausing.
	 */
	public final void awaitPausing() {
		synchronized (this) {
			while (!isInterrupted() && !isPausing()) {
				try {
					wait(100);
				} catch (final InterruptedException e) {
					// restore the interrupted status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Called when this thread is not paused and should do its work.
	 * 
	 * @throws InterruptedException
	 *             if the thread has been interrupted.
	 */
	protected abstract void doWork() throws InterruptedException;

	/**
	 * @return the priority which will be set for this thread.
	 */
	protected abstract ThreadPriority getThreadPriority();

	/**
	 * @return true if this thread has some work to do, false otherwise.
	 */
	protected abstract boolean hasWork();

	@Override
	public void interrupt() {
		// first acquire the monitor which is used to call wait()
		synchronized (this) {
			super.interrupt();
		}
	}

	/**
	 * @return true if this thread is currently pausing, false otherwise.
	 */
	public final synchronized boolean isPausing() {
		return this.pausing;
	}

	/**
	 * The thread should stop its work temporarily.
	 */
	public final synchronized void pause() {
		if (!this.shouldPause) {
			this.shouldPause = true;
			notify();
		}
	}

	/**
	 * The paused thread should continue with its work.
	 */
	public final synchronized void proceed() {
		if (this.shouldPause) {
			this.shouldPause = false;
			this.pausing = false;
			notify();
		}
	}

	@Override
	public final void run() {
		setName(getClass().getSimpleName());
		setPriority(getThreadPriority().prio);

		while (!isInterrupted()) {
			synchronized (this) {
				while (!isInterrupted() && (this.shouldPause || !hasWork())) {
					try {
						if (this.shouldPause) {
							this.pausing = true;
						}
						wait();
					} catch (final InterruptedException e) {
						// restore the interrupted status
						interrupt();
					}
				}
			}

			if (isInterrupted()) {
				break;
			}

			try {
				doWork();
			} catch (final InterruptedException e) {
				interrupt();
			} catch (final RuntimeException e) {
				L.warn(e);
			}
		}

		afterRun();
	}
}
