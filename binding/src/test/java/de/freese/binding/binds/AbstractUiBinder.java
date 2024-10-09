// Created: 09 Okt. 2024
package de.freese.binding.binds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public abstract class AbstractUiBinder<C, T> implements UiBinder<C, T> {
    private final List<Consumer<T>> consumers = new ArrayList<>();

    @Override
    public void addConsumer(final Consumer<T> consumer) {
        consumers.add(Objects.requireNonNull(consumer, "consumer required"));
    }

    protected void fireConsumers(final T value) {
        consumers.forEach(consumer -> consumer.accept(value));
    }
}
