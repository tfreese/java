// Created: 11 Aug. 2025
package de.freese.newbinding.constant;

import de.freese.newbinding.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractConstant<T> implements Property<T> {
    private final String name;
    private final T value;

    protected AbstractConstant(final T value, final String name) {
        super();

        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }
}
