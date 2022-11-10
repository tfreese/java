// Created: 08.07.2018
package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Index;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link MetaExporter}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMetaExporter implements MetaExporter
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see de.freese.metamodel.metagen.MetaExporter#export(javax.sql.DataSource, java.lang.String, java.lang.String)
     */
    @Override
    public List<Schema> export(final DataSource dataSource, final String schemaNamePattern, final String tableNamePattern) throws Exception
    {
        Objects.requireNonNull(dataSource, "dataSource required");

        List<Schema> schemas = generateSchemas(dataSource, schemaNamePattern);

        for (Schema schema : schemas)
        {
            // Tabellen des Schemas
            generateTables(dataSource, schema, tableNamePattern);

            if (schema.getTables().isEmpty())
            {
                continue;
            }

            // Sequences des Schemas
            generateSequences(dataSource, schema);

            // Spalten der Tabellen
            for (Table table : schema.getTables())
            {
                generateColumns(dataSource, table);
            }

            // PrimaryKeys der Tabellen
            for (Table table : schema.getTables())
            {
                generatePrimaryKeys(dataSource, table);
            }

            // ForeignKeys der Spalten
            for (Table table : schema.getTables())
            {
                generateForeignKeys(dataSource, table);
            }

            // Indices der Tabelle
            for (Table table : schema.getTables())
            {
                generateIndices(dataSource, table);
            }

            schema.validate();
        }

        return schemas;
    }

    /**
     * Erzeugt das Meta-Modell der Spalten einer Tabelle.
     */
    protected void createColumn(final Table table, final ResultSet resultSet) throws SQLException
    {
        String columnName = resultSet.getString("COLUMN_NAME");
        int dataType = resultSet.getInt("DATA_TYPE");
        String typeName = resultSet.getString("TYPE_NAME");
        int columnSize = resultSet.getInt("COLUMN_SIZE");
        int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
        String comment = resultSet.getString("REMARKS");
        boolean nullable = resultSet.getBoolean("NULLABLE");
        int tableIndex = resultSet.getInt("ORDINAL_POSITION");

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Column: {}.{}", table.getFullName(), columnName);
        }

        Column column = table.getColumn(columnName);
        column.setSqlType(dataType);
        column.setTypeName(typeName);
        column.setComment(comment);
        column.setNullable(nullable);
        column.setSize(columnSize);
        column.setDecimalDigits(decimalDigits);
        column.setTableIndex(tableIndex);

        // try
        // {
        // Charset charset = StandardCharsets.UTF_8;
        // column.setComment(new String(comment.getBytes(charset), charset));
        // }
        // catch (Exception ex)
        // {
        // getLogger().warn(ex.getMessage());
        // }
    }

    /**
     * Erzeugt das Meta-Modell eines PrimaryKeys einer Tabelle.
     */
    protected void createForeignKey(final Table table, final ResultSet resultSet) throws SQLException
    {
        String fkName = resultSet.getString("FK_NAME");
        String columnName = resultSet.getString("FKCOLUMN_NAME");
        String refTableName = resultSet.getString("PKTABLE_NAME");
        String refColumnName = resultSet.getString("PKCOLUMN_NAME");

        if (fkName.isBlank())
        {
            fkName = table.getName().toUpperCase() + "_FK";
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing ForeignKey: {} on {}.{} -> {}.{}", fkName, table.getFullName(), columnName, refTableName, refColumnName);
        }

        Column column = table.getColumn(columnName);

        Table refTable = table.getSchema().getTable(refTableName);
        Column refColumn = refTable.getColumn(refColumnName);

        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName(fkName);
        foreignKey.setColumn(column);
        foreignKey.setRefColumn(refColumn);

        column.setForeignKey(foreignKey);
    }

    /**
     * Erzeugt das Meta-Modell der Indices einer Tabelle.
     */
    protected void createIndices(final Table table, final ResultSet resultSet) throws SQLException
    {
        String indexName = resultSet.getString("INDEX_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        int keyColumnIndex = resultSet.getInt("ORDINAL_POSITION");

        // NON_UNIQUE = true
        boolean unique = !resultSet.getBoolean("NON_UNIQUE");

        if (indexName.isBlank())
        {
            if (!unique)
            {
                indexName = table.getName().toUpperCase() + "_IDX";
            }
            else
            {
                indexName = table.getName().toUpperCase() + "_UNQ";
            }
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Index: {} on {}.{}", indexName, table.getFullName(), columnName);
        }

        Column column = table.getColumn(columnName);

        if (!unique)
        {
            Index index = table.getIndex(indexName);
            index.addColumn(keyColumnIndex, column);
        }
        else
        {
            UniqueConstraint uniqueConstraint = table.getUniqueConstraint(indexName);
            uniqueConstraint.addColumn(keyColumnIndex, column);
        }
    }

    /**
     * Erzeugt das Meta-Modell eines PrimaryKeys einer Tabelle.
     */
    protected void createPrimaryKey(final Table table, final ResultSet resultSet) throws SQLException
    {
        String pkName = resultSet.getString("PK_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        int keyColumnIndex = resultSet.getInt("KEY_SEQ");

        if (pkName.isBlank())
        {
            pkName = table.getName().toUpperCase() + "_PK";
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing PrimaryKey: {} on {}.{}", pkName, table.getFullName(), columnName);
        }

        table.addPrimaryKeycolumn(pkName, keyColumnIndex, columnName);
    }

    /**
     * Erzeugt das Meta-Modell eines Schemas.
     */
    protected Schema createSchema(final ResultSet resultSet) throws SQLException
    {
        String schemaName = resultSet.getString("TABLE_SCHEM");

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Schema: {}", schemaName);
        }

        Schema schema = new Schema();
        schema.setName(schemaName);

        return schema;
    }

    /**
     * Erzeugt das Meta-Modell einer Tabelle.
     */
    protected void createTable(final Schema schema, final ResultSet resultSet) throws SQLException
    {
        // String catalog = resultSet.getString("TABLE_CAT");
        String schemaName = resultSet.getString("TABLE_SCHEM");
        String tableName = resultSet.getString("TABLE_NAME");
        String comment = resultSet.getString("REMARKS");

        // if (StringUtils.isBlank(schema) && StringUtils.isNotBlank(catalog))
        // {
        // schema = catalog;
        // }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Table: {}.{}", schemaName, tableName);
        }

        Table table = schema.getTable(tableName);
        table.setComment(comment);
    }

    protected void generateColumns(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getColumns(null, table.getSchema().getName(), table.getName(), null))
            {
                while (resultSet.next())
                {
                    createColumn(table, resultSet);
                }
            }
        }
    }

    protected void generateForeignKeys(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            // ForeignKeys von dieser Tabelle.
            try (ResultSet resultSet = metaData.getImportedKeys(null, table.getSchema().getName(), table.getName()))
            {
                while (resultSet.next())
                {
                    createForeignKey(table, resultSet);
                }
            }

            // ForeignKeys auf diese Tabelle.
            // try (ResultSet resultSet = metaData.getExportedKeys(null, table.getSchema().getName(), table.getName()))
            // {
            // while (resultSet.next())
            // {
            // createForeignKey(table, resultSet);
            // }
            // }
        }
    }

    protected void generateIndices(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getIndexInfo(null, table.getSchema().getName(), table.getName(), false, true))
            {
                while (resultSet.next())
                {
                    createIndices(table, resultSet);
                }
            }
        }
    }

    protected void generatePrimaryKeys(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getPrimaryKeys(null, table.getSchema().getName(), table.getName()))
            {
                while (resultSet.next())
                {
                    createPrimaryKey(table, resultSet);
                }
            }
        }
    }

    protected List<Schema> generateSchemas(final DataSource dataSource, final String schemaNamePattern) throws SQLException
    {
        List<Schema> schemas = new ArrayList<>();

        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getSchemas(null, schemaNamePattern))
            {
                while (resultSet.next())
                {
                    Schema schema = createSchema(resultSet);

                    schemas.add(schema);
                }
            }
        }

        return schemas;
    }

    protected abstract void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException;

    protected void generateTables(final DataSource dataSource, final Schema schema, final String tableNamePattern) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getTables(null, schema.getName(), tableNamePattern, new String[]
                    {
                            "TABLE"
                    }))
            {
                while (resultSet.next())
                {
                    createTable(schema, resultSet);
                }
            }
        }
    }

    protected Logger getLogger()
    {
        return this.logger;
    }
}
