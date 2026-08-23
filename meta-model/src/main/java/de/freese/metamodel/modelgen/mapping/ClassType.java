package de.freese.metamodel.modelgen.mapping;

import java.util.Objects;

import org.hibernate.mapping.Collection;

/**
 * @author Thomas Freese
 * @since 29.07.2018
 */
public record ClassType(Class<?> javaClass) implements Type {
    public ClassType(final Class<?> javaClass) {
        this.javaClass = Objects.requireNonNull(javaClass, "javaClass required");
    }

    @Override
    public boolean equalsClass(final Class<?> clazz) {
        return javaClass().equals(clazz);
    }

    @Override
    public String getSimpleName() {
        return javaClass().getSimpleName();
    }

    @Override
    public boolean isArray() {
        return javaClass().isArray();
    }

    @Override
    public boolean isAssoziation() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(javaClass());
    }

    @Override
    public boolean isPrimitive() {
        return javaClass().isPrimitive();
    }
}
