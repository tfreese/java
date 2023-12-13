// Created: 29.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.util.Objects;

import org.hibernate.mapping.Collection;

/**
 * @author Thomas Freese
 */
public class ClassType implements Type {
    private final Class<?> javaClass;

    public ClassType(final Class<?> javaClass) {
        super();

        this.javaClass = Objects.requireNonNull(javaClass, "javaClass required");
    }

    @Override
    public boolean equalsClass(final Class<?> clazz) {
        return getJavaClass().equals(clazz);
    }

    public Class<?> getJavaClass() {
        return this.javaClass;
    }

    @Override
    public String getSimpleName() {
        return getJavaClass().getSimpleName();
    }

    @Override
    public boolean isArray() {
        return getJavaClass().isArray();
    }

    @Override
    public boolean isAssoziation() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(getJavaClass());
    }

    @Override
    public boolean isPrimitive() {
        return getJavaClass().isPrimitive();
    }
}
