// Created: 09 Okt. 2024
package de.freese.binding.binds;

import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public interface UiBinder<C, T> {
    void addConsumer(Consumer<T> consumer);

    C getComponent();

    // Provider<T> provider(Callable<? extends T> value);
}
