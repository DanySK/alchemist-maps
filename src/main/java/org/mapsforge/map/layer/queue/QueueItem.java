/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.mapsforge.map.layer.queue;

class QueueItem<T extends Job> {
    private double priority;
    private final T object;

    protected QueueItem(final T object) {
        this.object = object;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof QueueItem)) {
            return false;
        }
        final QueueItem<?> other = (QueueItem<?>) obj;
        return this.object.equals(other.object);
    }

    public T getObject() {
        return object;
    }

    /**
     * @return the current priority of this job, will always be a positive
     *         number including zero.
     */
    public double getPriority() {
        return this.priority;
    }

    @Override
    public int hashCode() {
        return this.object.hashCode();
    }

    /**
     * @throws IllegalArgumentException
     *             if the given priority is negative or {@link Double#NaN}.
     */
    public void setPriority(final double priority) {
        if (priority < 0 || Double.isNaN(priority)) {
            throw new IllegalArgumentException("invalid priority: " + priority);
        }
        this.priority = priority;
    }
}
