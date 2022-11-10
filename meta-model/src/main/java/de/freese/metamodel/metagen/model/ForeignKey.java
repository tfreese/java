// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

/**
 * Enthält die MetaDaten eines ForeignKeys.
 *
 * @author Thomas Freese
 */
public class ForeignKey
{
    private Column column;

    private String name;

    private Column refColumn;

    public Column getColumn()
    {
        return this.column;
    }

    public String getName()
    {
        return this.name;
    }

    public Column getRefColumn()
    {
        return this.refColumn;
    }

    public void setColumn(final Column column)
    {
        this.column = column;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public void setRefColumn(final Column refColumn)
    {
        this.refColumn = refColumn;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ForeignKey [");
        builder.append("name=").append(this.name);
        builder.append(", ");
        builder.append(this.column.getTable().getName()).append(".").append(this.column.getName());
        builder.append(" -> ");
        builder.append(this.refColumn.getTable().getName()).append(".").append(this.refColumn.getName());
        builder.append("]");

        return builder.toString();
    }
}
