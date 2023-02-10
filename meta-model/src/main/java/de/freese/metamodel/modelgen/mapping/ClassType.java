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

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#equals(java.lang.Class)
     */
    @Override
    public boolean equals(final Class<?> clazz) {
        return getJavaClass().equals(clazz);
    }

    public Class<?> getJavaClass() {
        return this.javaClass;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return getJavaClass().getSimpleName();
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isArray()
     */
    @Override
    public boolean isArray() {
        return getJavaClass().isArray();
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isAssoziation()
     */
    @Override
    public boolean isAssoziation() {
        return false;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isCollection()
     */
    @Override
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(getJavaClass());
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isPrimitive()
     */
    @Override
    public boolean isPrimitive() {
        return getJavaClass().isPrimitive();
    }
}
