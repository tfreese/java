// Created: 29.08.23
package de.freese.dependency.utils;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public class ObjectPool<T> {
    private final Queue<T> idleObjects = new ConcurrentLinkedQueue<>();
    private final Supplier<T> supplier;

    public ObjectPool(final Supplier<T> supplier) {
        super();

        this.supplier = Objects.requireNonNull(supplier, "supplier required");
    }

    public void free(final T object) {
        if (object == null) {
            return;
        }

        idleObjects.offer(object);
    }

    public T get() {
        final T object = idleObjects.poll();

        return object != null ? object : supplier.get();
    }
}
