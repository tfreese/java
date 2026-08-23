package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines UniqueConstraint.
 *
 * @author Thomas Freese
 * @since 03.06.2016
 */
public class UniqueConstraint extends AbstractIndex {
    UniqueConstraint(final Table table, final String name) {
        super(table, name);
    }
}
