// Created: 11 Aug. 2025
package de.freese.newbinding.property;

import de.freese.newbinding.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractProperty<T> implements Property<T> {
    private final String name;

    protected AbstractProperty(final String name) {
        super();

        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
