// Created: 08.07.2018
package de.freese.metamodel.metagen.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enthält die MetaDaten einer Tabelle.
 *
 * @author Thomas Freese
 */
public class Table {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private final Map<String, Column> columns = new TreeMap<>();
    private final Map<String, Index> indices = new TreeMap<>();
    private final Map<String, UniqueConstraint> uniqueConstraints = new TreeMap<>();

    private String comment;
    private String name;
    private PrimaryKey primaryKey;
    private Schema schema;

    Table(final Schema schema, final String name) {
        super();

        this.schema = Objects.requireNonNull(schema, "schema required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    public void addPrimaryKeycolumn(final String keyName, final int keyColumnIndex, final String columnName) {
        if (this.primaryKey == null) {
            this.primaryKey = new PrimaryKey(this, keyName);
        }

        final Column column = getColumn(columnName);

        this.primaryKey.addColumn(keyColumnIndex, column);
    }

    public Column getColumn(final String name) {
        return this.columns.computeIfAbsent(name, key -> new Column(this, key));
    }

    public List<Column> getColumnsOrdered() {
        return this.columns.values().stream().sorted(Comparator.comparing(Column::getTableIndex)).toList();
        // return new ArrayList<>(this.columns.values());
    }

    public String getComment() {
        return this.comment;
    }

    public String getFullName() {
        if (getSchema().getName() != null) {
            return getSchema().getName() + "." + getName();
        }

        return getName();
    }

    public Index getIndex(final String name) {
        return this.indices.computeIfAbsent(name, key -> new Index(this, key));
    }

    public List<Index> getIndices() {
        return new ArrayList<>(this.indices.values());
    }

    public String getName() {
        return this.name;
    }

    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public UniqueConstraint getUniqueConstraint(final String name) {
        return this.uniqueConstraints.computeIfAbsent(name, key -> new UniqueConstraint(this, key));
    }

    public List<UniqueConstraint> getUniqueConstraints() {
        return new ArrayList<>(this.uniqueConstraints.values());
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSchema(final Schema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Table [");
        builder.append("schema=").append(getSchema().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }

    public void validate() {
        if (getPrimaryKey() != null) {
            // Entferne den UniqueConstraint, welcher nur eine Spalte enthält und diese der PrimaryKey ist.
            final Set<String> pkColumns = getPrimaryKey().getColumnMap().values().stream().map(Column::getName).collect(Collectors.toSet());

            for (UniqueConstraint uc : getUniqueConstraints()) {
                if (uc.getColumnMap().size() > 1) {
                    continue;
                }

                if (pkColumns.contains(uc.getColumnsOrdered().getFirst().getName())) {
                    getLogger().info("remove redundant UniqueConstraint {}; Cause: matches PrimaryKey", uc.getName());
                    this.uniqueConstraints.remove(uc.getName());
                }
            }

            // Entferne den Index, welcher nur eine Spalte enthält und diese der PrimaryKey ist.
            for (Index idx : getIndices()) {
                if (idx.getColumnMap().size() > 1) {
                    continue;
                }

                if (pkColumns.contains(idx.getColumnsOrdered().getFirst().getName())) {
                    getLogger().info("remove redundant Index {}; Cause: matches PrimaryKey", idx.getName());
                    this.indices.remove(idx.getName());
                }
            }
        }
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
