// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines Index.
 *
 * @author Thomas Freese
 */
public class Index extends AbstractIndex
{
    /**
     * Erstellt ein neues {@link Index} Object.
     *
     * @param table {@link Table}
     * @param name String
     */
    Index(final Table table, final String name)
    {
        super(table, name);
    }
}
