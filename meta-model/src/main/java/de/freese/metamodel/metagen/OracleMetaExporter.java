// Created: 23.07.2018
package de.freese.metamodel.metagen;

import java.sql.SQLException;

import javax.sql.DataSource;

import de.freese.metamodel.metagen.model.Schema;

/**
 * {@link MetaExporter} für Oracle.
 *
 * @author Thomas Freese
 */
public class OracleMetaExporter extends AbstractMetaExporter
{
    /**
     * @see de.freese.metamodel.metagen.AbstractMetaExporter#generateSequences(javax.sql.DataSource, de.freese.metamodel.metagen.model.Schema)
     */
    @Override
    protected void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException
    {
        throw new UnsupportedOperationException("not implemented");
    }

    // Oracle liefert die Kommentare auch in separaten Views.
    // select * from ALL_COL_COMMENTS where table_name = 'ANWENDUNG_SCHEMA' and comments is not null;
    // select * from USER_TAB_COMMENTS where table_name = 'ANWENDUNG_SCHEMA' and comments is not null;

    // void handleIndices(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // sql.append("select ui.index_name, uic.column_name from USER_INDEXES ui");
    // sql.append(" inner join USER_IND_COLUMNS uic on uic.index_name = ui.index_name and uic.table_name = ui.table_name"); // column_name, column_position
    // sql.append(" where ui.uniqueness = 'NONUNIQUE' and ui.table_name = ?");
    // sql.append(" order by ui.table_name asc, ui.index_name asc, uic.column_position asc");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getName());
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
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }

    // void handlePrimaryKeys(final DataSource dataSource, final Table table) throws SQLException
    // {
    // // select ui.table_name, ui.index_name, uic.column_name, uic.column_position from user_indexes ui
    // // inner join user_constraints uc on uc.constraint_name = ui.index_name and uc.table_name = ui.table_name -- constraint_type, status
    // // inner join user_ind_columns uic on uic.index_name = ui.index_name and uic.table_name = ui.table_name -- column_name, column_position
    // // where uc.status = 'ENABLED'
    // // and uc.constraint_type = 'P'
    // // order by ui.table_name asc, ui.index_name asc, uic.column_position asc;
    // }

    // void handleUniqueConstraints(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // // sql.append("select uc.constraint_name, ucc.column_name from user_constraints uc");
    // // sql.append(" inner join user_cons_columns ucc on ucc.table_name = uc.table_name and ucc.constraint_name = uc.constraint_name");
    // // sql.append(" where uc.table_name = ? and uc.constraint_type = 'U'");
    // sql.append("select ui.index_name, uic.column_name from USER_INDEXES ui");
    // sql.append(" inner join USER_CONSTRAINTS uc on uc.constraint_name = ui.index_name and uc.table_name = ui.table_name"); // constraint_type, status
    // sql.append(" inner join USER_IND_COLUMNS uic on uic.index_name = ui.index_name and uic.table_name = ui.table_name"); // column_name, column_position
    // sql.append(" where uc.status = 'ENABLED' and uc.constraint_type = 'U' and ui.table_name = ?");
    // sql.append(" order by ui.table_name asc, ui.index_name asc, uic.column_position asc");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getName());
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
