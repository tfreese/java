// Created: 02.06.2017
package de.freese.jsensors.backend.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import javax.sql.DataSource;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;

/**
 * {@link Backend} for database tables.<br>
 *
 * @author Thomas Freese
 */
public class JdbcBackend extends AbstractBatchBackend implements LifeCycle {
    private final DataSource dataSource;

    private final boolean exclusive;

    private final String tableName;

    /**
     * @param exclusive boolean; Table exclusive for only one {@link Sensor} -> no column 'NAME'
     */
    public JdbcBackend(final int batchSize, final DataSource dataSource, final String tableName, final boolean exclusive) {
        super(batchSize);

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.tableName = Objects.requireNonNull(tableName, "tableName required");
        this.exclusive = exclusive;
    }

    @Override
    public void start() {
        // Create Table if not exist.
        try (Connection con = this.dataSource.getConnection()) {
            DatabaseMetaData metaData = con.getMetaData();
            boolean exist = false;

            try (ResultSet tables = metaData.getTables(null, null, this.tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    // Table exist.
                    exist = true;
                }
            }

            if (!exist) {
                getLogger().info("Create table: {}", this.tableName);

                try (Statement stmt = con.createStatement()) {
                    // Create Table.
                    StringBuilder sql = new StringBuilder();
                    sql.append("CREATE TABLE ").append(this.tableName);

                    StringJoiner joiner = new StringJoiner(", ", " (", ")");

                    if (!this.exclusive) {
                        // With SensorName.
                        joiner.add("NAME VARCHAR(20) NOT NULL");
                    }

                    joiner.add("VALUE VARCHAR(50) NOT NULL");
                    joiner.add("TIMESTAMP BIGINT NOT NULL");

                    sql.append(joiner);

                    stmt.execute(sql.toString());

                    if (this.exclusive) {
                        // Without SensorName.
                        // String index = String.format("ALTER TABLE %s ADD CONSTRAINT TIMESTAMP_PK PRIMARY KEY (TIMESTAMP);", this.tableName);
                        String index = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (TIMESTAMP);", this.tableName, this.tableName);

                        stmt.execute(index);
                    }
                    else {
                        // With SensorName.
                        String index = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (NAME, TIMESTAMP);", this.tableName, this.tableName);

                        stmt.execute(index);

                        // These Indices existing by UNIQUE INDEX.
                        // index = String.format("CREATE INDEX NAME_IDX ON %s (NAME);", tableName);
                        // stmt.execute(index);
                        //
                        // index = String.format("CREATE INDEX TIMESTAMP_IDX ON %s (TIMESTAMP);", tableName);
                        // stmt.execute(index);
                    }
                }
            }
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        submit();
    }

    @Override
    protected void storeValues(final List<SensorValue> values) {
        if ((values == null) || values.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(this.tableName);

        if (this.exclusive) {
            // Without SensorName.
            sql.append(" (VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?)");
        }
        else {
            // With SensorName.
            sql.append(" (NAME, VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?, ?)");
        }

        try (Connection con = this.dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString())) {
            con.setAutoCommit(false);

            for (SensorValue sensorValue : values) {
                if (this.exclusive) {
                    // Without SensorName.
                    pstmt.setString(1, sensorValue.getValue());
                    pstmt.setLong(2, sensorValue.getTimestamp());
                }
                else {
                    // With SensorName.
                    pstmt.setString(1, sensorValue.getName());
                    pstmt.setString(2, sensorValue.getValue());
                    pstmt.setLong(3, sensorValue.getTimestamp());
                }

                pstmt.addBatch();
                // pstmt.clearParameters();
            }

            pstmt.executeBatch();

            con.commit();
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
