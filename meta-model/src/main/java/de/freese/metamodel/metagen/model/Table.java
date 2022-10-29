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
public class Table
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);
    /**
    *
    */
    private final Map<String, Column> columns = new TreeMap<>();
    /**
    *
    */
    private String comment;
    /**
    *
    */
    private final Map<String, Index> indices = new TreeMap<>();
    /**
     *
     */
    private String name;
    /**
     *
     */
    private PrimaryKey primaryKey;
    /**
    *
    */
    private Schema schema;
    /**
    *
    */
    private final Map<String, UniqueConstraint> uniqueConstraints = new TreeMap<>();

    /**
     * Erstellt ein neues {@link Table} Object.
     *
     * @param schema {@link Schema}
     * @param name String
     */
    Table(final Schema schema, final String name)
    {
        super();

        this.schema = Objects.requireNonNull(schema, "schema required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * @param keyName String
     * @param keyColumnIndex int
     * @param columnName String
     */
    public void addPrimaryKeycolumn(final String keyName, final int keyColumnIndex, final String columnName)
    {
        if (this.primaryKey == null)
        {
            this.primaryKey = new PrimaryKey(this, keyName);
        }

        Column column = getColumn(columnName);

        this.primaryKey.addColumn(keyColumnIndex, column);
    }

    /**
     * Liefert die Spalte mit dem Namen.
     *
     * @param name String
     *
     * @return {@link Column}
     */
    public Column getColumn(final String name)
    {
        return this.columns.computeIfAbsent(name, key -> new Column(this, key));
    }

    /**
     * Liefert alle Spalten der Tabelle sortiert nach dem TableIndex.
     *
     * @return {@link Table}
     */
    public List<Column> getColumnsOrdered()
    {
        return this.columns.values().stream().sorted(Comparator.comparing(Column::getTableIndex)).toList();
        // return new ArrayList<>(this.columns.values());
    }

    /**
     * @return String
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @return String
     */
    public String getFullName()
    {
        if (getSchema().getName() != null)
        {
            return getSchema().getName() + "." + getName();
        }

        return getName();
    }

    /**
     * Liefert den Index mit dem Namen.
     *
     * @param name String
     *
     * @return {@link Index}
     */
    public Index getIndex(final String name)
    {
        return this.indices.computeIfAbsent(name, key -> new Index(this, key));
    }

    /**
     * Liefert alle Indices der Tabelle.
     *
     * @return {@link List}
     */
    public List<Index> getIndices()
    {
        return new ArrayList<>(this.indices.values());
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return {@link PrimaryKey}
     */
    public PrimaryKey getPrimaryKey()
    {
        return this.primaryKey;
    }

    /**
     * @return {@link Schema}
     */
    public Schema getSchema()
    {
        return this.schema;
    }

    /**
     * Liefert den UniqueConstraint mit dem Namen.
     *
     * @param name String
     *
     * @return {@link UniqueConstraint}
     */
    public UniqueConstraint getUniqueConstraint(final String name)
    {
        return this.uniqueConstraints.computeIfAbsent(name, key -> new UniqueConstraint(this, key));
    }

    /**
     * Liefert alle UniqueConstraints der Tabelle.
     *
     * @return {@link List}
     */
    public List<UniqueConstraint> getUniqueConstraints()
    {
        return new ArrayList<>(this.uniqueConstraints.values());
    }

    /**
     * @param comment String
     */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param schema {@link Schema}
     */
    public void setSchema(final Schema schema)
    {
        this.schema = schema;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Table [");
        builder.append("schema=").append(getSchema().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }

    /**
     * Überprüfen des Models.
     */
    public void validate()
    {
        if (getPrimaryKey() != null)
        {
            // Entferne den UniqueConstraint, welcher nur eine Spalte enthält und diese der PrimaryKey ist.
            Set<String> pkColumns = getPrimaryKey().getColumnMap().values().stream().map(Column::getName).collect(Collectors.toSet());

            for (UniqueConstraint uc : getUniqueConstraints())
            {
                if (uc.getColumnMap().size() > 1)
                {
                    continue;
                }

                if (pkColumns.contains(uc.getColumnsOrdered().get(0).getName()))
                {
                    getLogger().info("remove redundant UniqueConstraint {}; Cause: matches PrimaryKey", uc.getName());
                    this.uniqueConstraints.remove(uc.getName());
                }
            }

            // Entferne den Index, welcher nur eine Spalte enthält und diese der PrimaryKey ist.
            for (Index idx : getIndices())
            {
                if (idx.getColumnMap().size() > 1)
                {
                    continue;
                }

                if (pkColumns.contains(idx.getColumnsOrdered().get(0).getName()))
                {
                    getLogger().info("remove redundant Index {}; Cause: matches PrimaryKey", idx.getName());
                    this.indices.remove(idx.getName());
                }
            }
        }
    }
}
