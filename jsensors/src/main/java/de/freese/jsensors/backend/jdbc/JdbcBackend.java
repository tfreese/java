package de.freese.jsensors.backend.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * {@link Backend} for database tables.<br>
 *
 * @author Thomas Freese
 * @since 02.06.2017
 */
public class JdbcBackend extends AbstractBatchBackend {
    public static class Builder {
        private int batchSize;
        private DataSource dataSource;
        private boolean exclusive;
        private String tableName;

        Builder() {
            super();
        }

        public Builder batchSize(final int batchSize) {
            this.batchSize = batchSize;

            return this;
        }

        public JdbcBackend build() throws SQLException {
            Objects.requireNonNull(dataSource, "dataSource required");
            Objects.requireNonNull(tableName, "tableName required");

            if (batchSize < 1) {
                throw new IllegalArgumentException("batchSize < 1: " + batchSize);
            }

            if (!existTable()) {
                createTable();
            }

            return new JdbcBackend(batchSize, dataSource, tableName, exclusive);
        }

        public Builder dataSource(final DataSource dataSource) {
            this.dataSource = dataSource;

            return this;
        }

        /**
         * @param exclusive; true=One table for one sensor, false=One Table for all sensors.
         */
        public Builder exclusive(final boolean exclusive) {
            this.exclusive = exclusive;

            return this;
        }

        public Builder tableName(final String tableName) {
            this.tableName = tableName;

            return this;
        }

        private void createTable() throws SQLException {
            LoggerFactory.getLogger(JdbcBackend.Builder.class).info("Create table: {}", tableName);

            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                final StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE ").append(tableName.toUpperCase(Locale.ROOT));

                final StringJoiner joiner = new StringJoiner(", ", " (", ")");

                if (!exclusive) {
                    // With SensorName.
                    joiner.add("NAME VARCHAR(20) NOT NULL");
                }

                joiner.add("VALUE VARCHAR(50) NOT NULL");
                joiner.add("TIMESTAMP BIGINT NOT NULL");

                sql.append(joiner);

                statement.execute(sql.toString());

                if (exclusive) {
                    // Without SensorName.
                    // String sqlIndex = String.format("ALTER TABLE %s ADD CONSTRAINT TIMESTAMP_PK PRIMARY KEY (TIMESTAMP);", tableName);
                    final String sqlIndex = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (TIMESTAMP);", tableName, tableName);

                    statement.execute(sqlIndex);
                }
                else {
                    // With SensorName.
                    final String sqlIndex = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (NAME, TIMESTAMP);", tableName, tableName);

                    statement.execute(sqlIndex);

                    // These Indices existing by UNIQUE INDEX.
                    // sqlIndex = String.format("CREATE INDEX NAME_IDX ON %s (NAME);", tableName);
                    // stmt.execute(sqlIndex);
                    //
                    // sqlIndex = String.format("CREATE INDEX TIMESTAMP_IDX ON %s (TIMESTAMP);", tableName);
                    // stmt.execute(sqlIndex);
                }
            }
        }

        private boolean existTable() throws SQLException {
            boolean tableExist = false;

            try (Connection connection = dataSource.getConnection()) {
                final DatabaseMetaData metaData = connection.getMetaData();

                try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                    if (tables.next()) {
                        // Table exist.
                        tableExist = true;
                    }
                }
            }

            return tableExist;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final DataSource dataSource;
    private final boolean exclusive;
    private final String tableName;

    /**
     * @param exclusive boolean; Table exclusive for only one {@link Sensor} -> no column 'NAME'
     */
    JdbcBackend(final int batchSize, final DataSource dataSource, final String tableName, final boolean exclusive) {
        super(batchSize);

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.tableName = Objects.requireNonNull(tableName, "tableName required");
        this.exclusive = exclusive;
    }

    @Override
    protected void storeValues(final List<SensorValue> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        final StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName);

        if (exclusive) {
            // Without SensorName.
            sql.append(" (VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?)");
        }
        else {
            // With SensorName.
            sql.append(" (NAME, VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?, ?)");
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString())) {
            con.setAutoCommit(false);

            for (final SensorValue sensorValue : values) {
                if (exclusive) {
                    // Without SensorName.
                    pstmt.setString(1, sensorValue.value());
                    pstmt.setLong(2, sensorValue.timestamp());
                }
                else {
                    // With SensorName.
                    pstmt.setString(1, sensorValue.name());
                    pstmt.setString(2, sensorValue.value());
                    pstmt.setLong(3, sensorValue.timestamp());
                }

                pstmt.addBatch();
                // pstmt.clearParameters();
            }

            pstmt.executeBatch();

            con.commit();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
