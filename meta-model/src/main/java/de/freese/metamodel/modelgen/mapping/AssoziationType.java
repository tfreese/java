// Created: 29.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class AssoziationType implements Type
{
    /**
     *
     */
    private boolean isCollection;
    /**
     *
     */
    private final String simpleName;

    /**
     * Erstellt ein neues {@link AssoziationType} Object.
     *
     * @param simpleName String
     */
    public AssoziationType(final String simpleName)
    {
        super();

        this.simpleName = Objects.requireNonNull(simpleName, "simpleName required");
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#equals(java.lang.Class)
     */
    @Override
    public boolean equals(final Class<?> clazz)
    {
        return false;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#getSimpleName()
     */
    @Override
    public String getSimpleName()
    {
        return this.simpleName;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isArray()
     */
    @Override
    public boolean isArray()
    {
        return false;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isAssoziation()
     */
    @Override
    public boolean isAssoziation()
    {
        return true;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isCollection()
     */
    @Override
    public boolean isCollection()
    {
        return this.isCollection;
    }

    /**
     * @see de.freese.metamodel.modelgen.mapping.Type#isPrimitive()
     */
    @Override
    public boolean isPrimitive()
    {
        return false;
    }

    /**
     * @param isCollection boolean
     */
    public void setCollection(final boolean isCollection)
    {
        this.isCollection = isCollection;
    }
}
