// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines UniqueConstraint.
 *
 * @author Thomas Freese
 */
public class UniqueConstraint extends AbstractIndex
{
    /**
     * Erstellt ein neues {@link UniqueConstraint} Object.
     *
     * @param table {@link Table}
     * @param name String
     */
    UniqueConstraint(final Table table, final String name)
    {
        super(table, name);
    }
}
