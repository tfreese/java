// Created: 25.07.2018
package de.freese.metamodel.modelgen.mapping;

/**
 * @author Thomas Freese
 */
public interface Type {
    boolean equalsClass(Class<?> clazz);

    String getSimpleName();

    boolean isArray();

    boolean isAssoziation();

    boolean isCollection();

    boolean isPrimitive();
}
