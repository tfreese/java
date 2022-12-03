// Created: 08.07.2018
package de.freese.metamodel;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public final class PrintDatabaseMetaDataMain
{
    public static void main(final String[] args) throws Exception
    {
        DataSource dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");

        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            // @formatter:off
            System.out.printf("%n%s: %d/%d, %s%n",
                              metaData.getDatabaseProductName(),
                              metaData.getDatabaseMajorVersion(),
                              metaData.getDatabaseMinorVersion(),
                              metaData.getDatabaseProductVersion());
            // @formatter:on

            // "PUBLIC"
            String schema = "PUBLIC";

            // "T_%"
            String table = "T_%";

            printCatalogsAndSchemas(connection);
            printClientInfoAndClientProperties(connection);
            printTables(connection, schema, null);
            printColumns(connection, schema, table);
            printPrimaryKeys(connection, schema, "T_PERSON");
            printPrimaryKeys(connection, schema, "T_ADDRESS");

            printForeignKeys(connection, schema, "T_PERSON");
            printForeignKeys(connection, schema, "T_ADDRESS");
            printIndices(connection, schema, "T_PERSON");
            printSequences(connection, schema);
        }
        finally
        {
            TestUtil.closeDataSource(dataSource);
        }
    }

    private static void printCatalogsAndSchemas(final Connection connection) throws SQLException
    {
        System.out.printf("%nConnection Catalog: %s%n", connection.getCatalog());

        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getCatalogs())
        {
            System.out.println("\nCatalogs:");
            write(rs, System.out);
        }

        try (ResultSet rs = metaData.getSchemas())
        {
            System.out.println("\nSchemas");
            write(rs, System.out);
        }
    }

    private static void printClientInfoAndClientProperties(final Connection connection) throws SQLException
    {
        Properties properties = connection.getClientInfo();

        if (properties != null)
        {
            System.out.println("\nClientInfo:");
            properties.forEach((key, value) ->
                    System.out.printf("ClientInfo: %s - %s%n", key, value)
            );
        }

        // DatabaseMetaData metaData = connection.getMetaData();
        //
        // try (ResultSet rs = metaData.getClientInfoProperties())
        // {
        // System.out.println("\nClientInfoProperties:");
        // write(rs, System.out);
        // }
    }

    private static void printColumns(final Connection connection, final String schema, final String tableName) throws SQLException
    {
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet columns = metaData.getColumns(null, schema, tableName, null))
        {
            System.out.println("\nSpalten:");
            write(columns, System.out);
        }
    }

    private static void printForeignKeys(final Connection connection, final String schema, final String tableName) throws SQLException
    {
        DatabaseMetaData metaData = connection.getMetaData();

        // ForeignKeys von dieser Tabelle.
        try (ResultSet foreignKeys = metaData.getImportedKeys(schema, schema, tableName))
        {
            System.out.printf("%n%s: ForeignKeys-ImportedKeys%n", tableName);
            write(foreignKeys, System.out);
        }

        // ForeignKeys auf diese Tabelle.
        try (ResultSet refForeignKeys = metaData.getExportedKeys(schema, schema, tableName))
        {
            System.out.printf("%n%s: ForeignKeys-ExportedKeys%n", tableName);
            write(refForeignKeys, System.out);
        }
    }

    private static void printIndices(final Connection connection, final String schema, final String tableName) throws SQLException
    {
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet unique = metaData.getIndexInfo(schema, schema, tableName, false, true))
        {
            // NON_UNIQUE = true
            System.out.println("\nIndices:");
            write(unique, System.out);
        }

        try (ResultSet unique = metaData.getIndexInfo(schema, schema, tableName, true, true))
        {
            // NON_UNIQUE = false
            System.out.println("\nUnique Constraints:");
            write(unique, System.out);
        }
    }

    private static void printPrimaryKeys(final Connection connection, final String schema, final String tableName) throws SQLException
    {
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet primaryKeys = metaData.getPrimaryKeys(schema, schema, tableName))
        {
            System.out.println("\nPrimaryKeys:");
            write(primaryKeys, System.out);
        }
    }

    private static void printSequences(final Connection connection, final String schema) throws SQLException
    {
        // information_schema.sequences
        // information_schema.system_sequences

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = ?"))
        {
            preparedStatement.setString(1, schema);

            try (ResultSet sequences = preparedStatement.executeQuery())
            {
                System.out.println("\nSequences:");
                write(sequences, System.out);
            }
        }
    }

    private static void printTables(final Connection connection, final String schema, final String tableNamePattern) throws SQLException
    {
        String[] types =
                {
                        "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM", "SEQUENCE"
                };

        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet tables = metaData.getTables(null, schema, tableNamePattern, types))
        {
            System.out.println("\nTabellen:");
            write(tables, System.out);
        }
    }

    private static void write(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        TestUtil.write(resultSet, ps);
    }

    private PrintDatabaseMetaDataMain()
    {
        super();
    }
}
