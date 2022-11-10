// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines UniqueConstraint.
 *
 * @author Thomas Freese
 */
public class UniqueConstraint extends AbstractIndex
{
    UniqueConstraint(final Table table, final String name)
    {
        super(table, name);
    }
}
