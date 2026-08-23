package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines PrimaryKeys.
 *
 * @author Thomas Freese
 * @since 03.06.2016
 */
public class PrimaryKey extends AbstractIndex {
    PrimaryKey(final Table table, final String name) {
        super(table, name);
    }
}
