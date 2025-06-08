// Created: 03.06.2016
package de.freese.metamodel.metagen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Basis-Implementierung: Enthält die MetaDaten eines Index.
 *
 * @author Thomas Freese
 */
public abstract class AbstractIndex {
    private final Map<Integer, Column> columns = new TreeMap<>();

    private String name;
    private Table table;

    AbstractIndex(final Table table, final String name) {
        super();

        this.table = Objects.requireNonNull(table, "table required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    public void addColumn(final int keyIndex, final Column column) {
        columns.put(keyIndex, column);
    }

    /**
     * Liefert alle Spalten des Indexes sortiert nach KeyIndex.
     */
    public List<Column> getColumnsOrdered() {
        // return columns.values().stream().sorted(Comparator.comparing(Column::getName)).toList();
        return new ArrayList<>(columns.values());
    }

    public String getName() {
        return name;
    }

    public Table getTable() {
        return table;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTable(final Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append(" table=").append(table);
        builder.append(", name=").append(name);
        builder.append("]");

        return builder.toString();
    }

    protected Map<Integer, Column> getColumnMap() {
        return columns;
    }
}
