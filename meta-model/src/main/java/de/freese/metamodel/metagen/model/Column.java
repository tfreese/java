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
public class Column {
    private static final int UNDEFINED = -1;
    /**
     * Columns anderer Tabellen, die auf diese Column zeigen.
     */
    private final List<Column> reverseForeignKeys = new ArrayList<>();

    private String comment;

    private int decimalDigits = UNDEFINED;

    private ForeignKey foreignKey;

    private String name;

    private boolean nullable;

    private int size = UNDEFINED;
    /**
     * @see Types
     */
    private int sqlType = Types.NULL;

    private Table table;

    private int tableIndex;

    private String typeName;

    Column(final Table table, final String name) {
        super();

        this.table = Objects.requireNonNull(table, "table required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    public String getComment() {
        return this.comment;
    }

    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    public ForeignKey getForeignKey() {
        return this.foreignKey;
    }

    public JDBCType getJdbcType() {
        return JDBCType.valueOf(getSqlType());
    }

    public String getName() {
        return this.name;
    }

    /**
     * Columns anderer Tabellen, die auf diese Column zeigen.
     */
    public List<Column> getReverseForeignKeys() {
        return new ArrayList<>(this.reverseForeignKeys);
    }

    public int getSize() {
        return this.size;
    }

    /**
     * @see Types
     */
    public int getSqlType() {
        return this.sqlType;
    }

    public Table getTable() {
        return this.table;
    }

    public int getTableIndex() {
        return this.tableIndex;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public boolean hasDecimalDigits() {
        return getDecimalDigits() != UNDEFINED;
    }

    public boolean hasSize() {
        return getSize() != UNDEFINED;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    /**
     * Liefert true, wenn die Column zu den PrimaryKey-Columns gehört.
     */
    public boolean isPrimaryKey() {
        PrimaryKey pk = getTable().getPrimaryKey();

        if (pk == null) {
            return false;
        }

        return pk.getColumnsOrdered().contains(this);
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public void setDecimalDigits(final int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public void setForeignKey(final ForeignKey foreignKey) {
        this.foreignKey = foreignKey;

        this.foreignKey.getRefColumn().addReverseForeignKey(this);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * @see Types
     */
    public void setSqlType(final int sqlType) {
        this.sqlType = sqlType;
    }

    public void setTable(final Table table) {
        this.table = table;
    }

    public void setTableIndex(final int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
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
     */
    void addReverseForeignKey(final Column column) {
        this.reverseForeignKeys.add(column);
    }
}
