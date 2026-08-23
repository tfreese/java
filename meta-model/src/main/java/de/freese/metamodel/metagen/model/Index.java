package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines Index.
 *
 * @author Thomas Freese
 * @since 03.06.2016
 */
public class Index extends AbstractIndex {
    Index(final Table table, final String name) {
        super(table, name);
    }
}
