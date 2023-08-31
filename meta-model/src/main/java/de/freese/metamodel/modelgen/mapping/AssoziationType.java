// Created: 29.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class AssoziationType implements Type {
    private final String simpleName;
    private boolean isCollection;

    public AssoziationType(final String simpleName) {
        super();

        this.simpleName = Objects.requireNonNull(simpleName, "simpleName required");
    }

    @Override
    public boolean equals(final Class<?> clazz) {
        return false;
    }

    @Override
    public String getSimpleName() {
        return this.simpleName;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isAssoziation() {
        return true;
    }

    @Override
    public boolean isCollection() {
        return this.isCollection;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    public void setCollection(final boolean isCollection) {
        this.isCollection = isCollection;
    }
}
