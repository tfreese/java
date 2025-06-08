// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines ForeignKeys.
 *
 * @author Thomas Freese
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
        final StringBuilder builder = new StringBuilder();
        builder.append("ForeignKey [");
        builder.append("name=").append(name);
        builder.append(", ");
        builder.append(column.getTable().getName()).append(".").append(column.getName());
        builder.append(" -> ");
        builder.append(refColumn.getTable().getName()).append(".").append(refColumn.getName());
        builder.append("]");

        return builder.toString();
    }
}
