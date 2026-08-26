package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines ForeignKeys.
 *
 * @author Thomas Freese
 * @since 03.06.2016
 */
public class ForeignKey {
    private Column column;
    private String name;
    private Column refColumn;

    public Column getColumn() {
        return column;
    }

    public String getName() {
        return name;
    }

    public Column getRefColumn() {
        return refColumn;
    }

    public void setColumn(final Column column) {
        this.column = column;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setRefColumn(final Column refColumn) {
        this.refColumn = refColumn;
    }

    @Override
    public String toString() {
        return "ForeignKey ["
                + "name=" + name
                + ", "
                + column.getTable().getName() + "." + column.getName()
                + " -> " + refColumn.getTable().getName() + "." + refColumn.getName()
                + "]";
    }
}
