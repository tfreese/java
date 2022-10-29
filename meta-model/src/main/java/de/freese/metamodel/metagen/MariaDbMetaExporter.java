// Created: 23.07.2018
package de.freese.metamodel.metagen;

import java.sql.SQLException;

import javax.sql.DataSource;

import de.freese.metamodel.metagen.model.Schema;

/**
 * {@link MetaExporter} für MariaDB.
 *
 * @author Thomas Freese
 */
public class MariaDbMetaExporter extends AbstractMetaExporter
{
    /**
     * @see de.freese.metamodel.metagen.AbstractMetaExporter#generateSequences(javax.sql.DataSource, de.freese.metamodel.metagen.model.Schema)
     */
    @Override
    protected void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException
    {
        throw new UnsupportedOperationException("not implemented");
    }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param table {@link Table}
    // * @throws SQLException Falls was schief geht.
    // */
    // void handleIndices(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // sql.append("select index_name, column_name from INFORMATION_SCHEMA.STATISTICS");
    // sql.append(" where non_unique = 1 and table_schema = ? and table_name = ? ");
    // sql.append(" order by seq_in_index asc");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // while (rs.next())
    // {
    // // String name = rs.getString("INDEX_NAME");
    // // String column = rs.getString("COLUMN_NAME");
    //
    // // Index index = table.getIndex(name);
    // // Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);;
    // }
    // }
    // }
    // }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param table {@link Table}
    // * @throws SQLException Falls was schief geht.
    // */
    // void handleUniqueConstraints(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // sql.append("select tc.constraint_name, kcu.column_name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc");
    // sql.append(" inner join INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu on kcu.constraint_name = tc.constraint_name");
    // sql.append(" where tc.table_schema = ? and tc.table_name = ? and tc.constraint_type = 'UNIQUE'");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // while (rs.next())
    // {
    // // String name = rs.getString("CONSTRAINT_NAME");
    // // String column = rs.getString("COLUMN_NAME");
    //
    // // UniqueConstraint index = table.getUniqueConstraint(name);
    // // Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }
}
