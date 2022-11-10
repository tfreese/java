// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines PrimaryKeys.
 *
 * @author Thomas Freese
 */
public class PrimaryKey extends AbstractIndex
{
    PrimaryKey(final Table table, final String name)
    {
        super(table, name);
    }
}
