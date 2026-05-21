// Created: 29.08.23
package de.freese.dependency.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObjectPool<T> {
    private final Queue<T> idleObjects = new ConcurrentLinkedQueue<>();

    public void free(final T object) {
        if (object == null) {
            return;
        }

        idleObjects.offer(object);
    }

    public T get() {
        final T object = idleObjects.poll();

        return object != null ? object : create();
    }

    protected abstract T create();
}
