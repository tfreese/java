// Created: 08.07.2018
package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Sequence;

/**
 * {@link MetaExporter} für HSQLDB.
 *
 * @author Thomas Freese
 */
public class HsqldbMetaExporter extends AbstractMetaExporter {
    @Override
    protected void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException {
        final StringBuilder sql = new StringBuilder();
        sql.append("select SEQUENCE_NAME, START_WITH, INCREMENT, NEXT_VALUE");
        sql.append(" from INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
        sql.append(" where SEQUENCE_SCHEMA = ?");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            preparedStatement.setString(1, schema.getName());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final String sequenceName = resultSet.getString("SEQUENCE_NAME");
                    final long startWith = resultSet.getLong("START_WITH");
                    final long increment = resultSet.getLong("INCREMENT");
                    final long nextValue = resultSet.getLong("NEXT_VALUE");

                    final Sequence sequence = schema.getSequence(sequenceName);
                    sequence.setStartWith(startWith);
                    sequence.setIncrement(increment);
                    sequence.setNextValue(nextValue);
                }
            }
        }
    }

    // void handleIndices(final DataSource dataSource, final Table table) throws SQLException {
    // final StringBuilder sql = new StringBuilder();
    // sql.append("select index_name, column_name FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO");
    // sql.append(" where non_unique = TRUE and table_schem = ? and table_name = ? and index_name not like 'SYS_%'");
    // sql.append(" order by ordinal_position asc");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString())) {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery()) {
    // while (rs.next()) {
    // // final String name = rs.getString("INDEX_NAME");
    // // final String columnName = rs.getString("COLUMN_NAME");
    //
    // // final Index index = table.getIndex(name);
    // // final Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }

    // void handleUniqueConstraints(final DataSource dataSource, final Table table) throws SQLException {
    // final StringBuilder sql = new StringBuilder();
    // sql.append("select ts.constraint_name, ccu.column_name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS ts");
    // sql.append(" inner join INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu on ccu.table_name = ts.table_name");
    // sql.append(" and ccu.constraint_name = ts.constraint_name");
    // sql.append(" where ts.table_schema = ? and ts.table_name = ? and ts.constraint_type = 'UNIQUE'");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString())) {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery()) {
    // while (rs.next()) {
    // // final String name = rs.getString("CONSTRAINT_NAME");
    // // final String columnName = rs.getString("COLUMN_NAME");
    //
    // // final UniqueConstraint index = table.getUniqueConstraint(name);
    // // final Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }

    // void handleViews(final DataSource dataSource, final Schema schema) throws SQLException {
    // // final List<View> viewList = new ArrayList<>();
    //
    // try (Connection con = dataSource.getConnection()) {
    // // DatabaseMetaData metaData = con.getMetaData();
    //
    // final StringBuilder sql = new StringBuilder();
    // sql.append("select table_name, view_definition");
    // sql.append(" from INFORMATION_SCHEMA.VIEWS");
    // sql.append(" where table_schema = ?");
    //
    // try (PreparedStatement stmt = con.prepareStatement(sql.toString())) {
    // stmt.setString(1, schema.getName());
    //
    // try (ResultSet rs = stmt.executeQuery()) {
    // while (rs.next()) {
    // final String name = rs.getString("TABLE_NAME");
    // // final String def = rs.getString("VIEW_DEFINITION");
    //
    // // viewList.add(new View(schema, name, def));
    //
    // if (getLogger().isDebugEnabled()) {
    // getLogger().debug("Processing view: {}", name);
    // }
    // }
    // }
    // }
    //
    // // Spalten
    // // sql = new StringBuilder();
    // // sql.append("select column_name");
    // // sql.append(" from INFORMATION_SCHEMA.VIEW_COLUMN_USAGE");
    // // sql.append(" where view_schema = ? and view_name = ?");
    //
    // // for (View view : viewList) {
    // // // try (PreparedStatement stmt = con.prepareStatement(sql.toString())) {
    // // // stmt.setString(1, view.getSchema());
    // // // stmt.setString(2, view.getName());
    // // //
    // // // try (ResultSet columns = stmt.executeQuery()) {
    // //
    // // try (ResultSet columns = metaData.getColumns(schema.getName(), schema.getName(), view.getName(), null)) {
    // // while (columns.next()) {
    // // String columnName = columns.getString("COLUMN_NAME");
    // //
    // // view.addColumn(columnName);
    // // }
    // // }
    // // }
    // }
    // }
}
