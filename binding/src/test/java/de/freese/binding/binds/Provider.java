// Created: 12 Okt. 2024
package de.freese.binding.binds;

import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public interface Provider<T> {
    void register(Consumer<T> consumer);
}
