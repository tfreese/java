// Created: 25.07.2018
package de.freese.metamodel.modelgen.mapping;

/**
 * @author Thomas Freese
 */
public interface Type
{
    /**
     * @param clazz {@link Class}
     *
     * @return boolean
     */
    boolean equals(Class<?> clazz);

    /**
     * @return String
     */
    String getSimpleName();

    /**
     * @return boolean
     */
    boolean isArray();

    /**
     * @return boolean
     */
    boolean isAssoziation();

    /**
     * @return boolean
     */
    boolean isCollection();

    /**
     * @return boolean
     */
    boolean isPrimitive();
}
