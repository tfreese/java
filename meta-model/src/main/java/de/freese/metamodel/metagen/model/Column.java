// Created: 08.07.2018
package de.freese.metamodel.metagen.model;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Enthält die MetaDaten einer Spalte.
 *
 * @author Thomas Freese
 */
public class Column
{
    /**
     *
     */
    private static final int UNDEFINED = -1;
    /**
     * Columns anderer Tabellen, die auf diese Column zeigen.
     */
    private final List<Column> reverseForeignKeys = new ArrayList<>();
    /**
     *
     */
    private String comment;
    /**
     *
     */
    private int decimalDigits = UNDEFINED;
    /**
     *
     */
    private ForeignKey foreignKey;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private boolean nullable;
    /**
     *
     */
    private int size = UNDEFINED;
    /**
     * @see Types
     */
    private int sqlType = Types.NULL;
    /**
     *
     */
    private Table table;
    /**
     *
     */
    private int tableIndex;
    /**
     *
     */
    private String typeName;

    /**
     * Erstellt ein neues {@link Column} Object.
     *
     * @param table {@link Table}
     * @param name String
     */
    Column(final Table table, final String name)
    {
        super();

        this.table = Objects.requireNonNull(table, "table required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * @return String
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @return int
     */
    public int getDecimalDigits()
    {
        return this.decimalDigits;
    }

    /**
     * @return {@link ForeignKey}
     */
    public ForeignKey getForeignKey()
    {
        return this.foreignKey;
    }

    /**
     * @return {@link JDBCType}
     */
    public JDBCType getJdbcType()
    {
        return JDBCType.valueOf(getSqlType());
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Columns anderer Tabellen, die auf diese Column zeigen.
     *
     * @return {@link List}
     */
    public List<Column> getReverseForeignKeys()
    {
        return new ArrayList<>(this.reverseForeignKeys);
    }

    /**
     * @return int
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * @return int
     *
     * @see Types
     */
    public int getSqlType()
    {
        return this.sqlType;
    }

    /**
     * @return {@link Table}
     */
    public Table getTable()
    {
        return this.table;
    }

    /**
     * @return int
     */
    public int getTableIndex()
    {
        return this.tableIndex;
    }

    /**
     * @return String
     */
    public String getTypeName()
    {
        return this.typeName;
    }

    /**
     * @return boolean
     */
    public boolean hasDecimalDigits()
    {
        return getDecimalDigits() != UNDEFINED;
    }

    /**
     * @return boolean
     */
    public boolean hasSize()
    {
        return getSize() != UNDEFINED;
    }

    /**
     * @return boolean
     */
    public boolean isNullable()
    {
        return this.nullable;
    }

    /**
     * Liefert true, wenn die Column zu den PrimaryKey-Columns gehört.
     *
     * @return boolean
     */
    public boolean isPrimaryKey()
    {
        PrimaryKey pk = getTable().getPrimaryKey();

        if (pk == null)
        {
            return false;
        }

        return pk.getColumnsOrdered().contains(this);
    }

    /**
     * @param comment String
     */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /**
     * @param decimalDigits int
     */
    public void setDecimalDigits(final int decimalDigits)
    {
        this.decimalDigits = decimalDigits;
    }

    /**
     * @param foreignKey {@link ForeignKey}
     */
    public void setForeignKey(final ForeignKey foreignKey)
    {
        this.foreignKey = foreignKey;

        this.foreignKey.getRefColumn().addReverseForeignKey(this);
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param nullable boolean
     */
    public void setNullable(final boolean nullable)
    {
        this.nullable = nullable;
    }

    /**
     * @param size int
     */
    public void setSize(final int size)
    {
        this.size = size;
    }

    /**
     * @param sqlType int
     *
     * @see Types
     */
    public void setSqlType(final int sqlType)
    {
        this.sqlType = sqlType;
    }

    /**
     * @param table {@link Table}
     */
    public void setTable(final Table table)
    {
        this.table = table;
    }

    /**
     * @param tableIndex int
     */
    public void setTableIndex(final int tableIndex)
    {
        this.tableIndex = tableIndex;
    }

    /**
     * @param typeName String
     */
    public void setTypeName(final String typeName)
    {
        this.typeName = typeName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Column [");
        builder.append("schema=").append(getTable().getSchema().getName());
        builder.append(", table=").append(getTable().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }

    /**
     * Hinzufügen einer Column einer anderen Tabelle, die auf diese Column zeigt.
     *
     * @param column {@link Column}
     */
    void addReverseForeignKey(final Column column)
    {
        this.reverseForeignKeys.add(column);
    }
}
