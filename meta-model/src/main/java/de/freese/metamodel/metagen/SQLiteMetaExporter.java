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
public class SQLiteMetaExporter extends AbstractMetaExporter {
    /**
     * @see de.freese.metamodel.metagen.AbstractMetaExporter#generateSequences(javax.sql.DataSource, de.freese.metamodel.metagen.model.Schema)
     */
    @Override
    protected void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    // void handleUniqueConstraints(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // // sql.append("select * from sqlite_master where tbl_name = ? and type = 'index'");
    // sql.append("PRAGMA INDEX_LIST('" + table.getName() + "') where UNIQUE = 1");
    // // sql.append("PRAGMA INDEX_INFO('" + table.getName() + "')");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // // stmt.setString(1, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // // Utils.write(rs, System.out);
    // }
    // }
    // }
}
