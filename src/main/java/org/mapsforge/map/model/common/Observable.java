/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.model.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Observable {
	private static final String OBSERVER_MUST_NOT_BE_NULL = "observer must not be null";
	private final List<Observer> observers = new CopyOnWriteArrayList<Observer>();

	protected Observable() {
		// do nothing
	}

	public final void addObserver(final Observer observer) {
		if (observer == null) {
			throw new IllegalArgumentException(OBSERVER_MUST_NOT_BE_NULL);
		} else if (this.observers.contains(observer)) {
			throw new IllegalArgumentException("observer is already registered: " + observer);
		}
		this.observers.add(observer);
	}

	protected final void notifyObservers() {
		for (final Observer observer : this.observers) {
			observer.onChange();
		}
	}

	public final void removeObserver(final Observer observer) {
		if (observer == null) {
			throw new IllegalArgumentException(OBSERVER_MUST_NOT_BE_NULL);
		} else if (!this.observers.contains(observer)) {
			throw new IllegalArgumentException("observer is not registered: " + observer);
		}
		this.observers.remove(observer);
	}
}
