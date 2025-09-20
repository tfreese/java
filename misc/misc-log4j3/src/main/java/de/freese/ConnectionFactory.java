package de.freese;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCPool;

/**
 * @author Thomas Freese
 */
public final class ConnectionFactory {
    /**
     * @author Thomas Freese
     */
    private static final class ConnectionFactoryHolder {
        private static final ConnectionFactory INSTANCE = new ConnectionFactory();

        private ConnectionFactoryHolder() {
            super();
        }
    }

    public static Connection getDatabaseConnection() throws SQLException {
        return ConnectionFactoryHolder.INSTANCE.getDataSource().getConnection();
    }

    public static ConnectionFactory getInstance() {
        return ConnectionFactoryHolder.INSTANCE;
    }

    private final DataSource dataSource;

    private ConnectionFactory() {
        super();

        final Path dbPath = Paths.get(System.getProperty("java.io.tmpdir"), "db", "logging", "logging");

        final JDBCPool pool = new JDBCPool(3);
        pool.setUrl("jdbc:hsqldb:file:" + dbPath + ";shutdown=true");
        pool.setUser("sa");
        pool.setPassword("sa");

        // where EVENT_DATE > TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_DATE)
        // where EVENT_DATE > DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)

        this.dataSource = pool;

        initDbLog4j();
    }

    public void close() throws Exception {
        System.out.printf("%s_ConnectionFactory.close%n", Thread.currentThread().getName());

        try (Connection connection = getDataSource().getConnection()) {
            final String productName = connection.getMetaData().getDatabaseProductName();

            // Handled already by hsql with 'shutdown=true'.
            if (productName.contains("h2") || productName.contains("hsql")) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("SHUTDOWN COMPACT");
                }
            }
        }
        catch (SQLException _) {
            // Ignore
        }

        if (getDataSource() instanceof JDBCPool p) {
            p.close(1);
        }
        else if (getDataSource() instanceof AutoCloseable ac) {
            ac.close();
        }
        else if (getDataSource() instanceof Closeable c) {
            c.close();
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private boolean existTable(final String tableName) {
        try (Connection connection = getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initDbLog4j() {
        if (existTable("LOGGING")) {
            return;
        }

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            // MESSAGE LONGVARCHAR
            // THROWABLE LONGVARCHAR
            final String sql = """
                    create table if not exists LOGGING (
                        ID BIGINT NOT NULL PRIMARY KEY,
                        EVENT_DATE TIMESTAMP NOT NULL,
                        HOST VARCHAR(256) NOT NULL,
                        MODUL VARCHAR(256) NOT NULL,
                        LEVEL VARCHAR(256) NOT NULL,
                        THREAD VARCHAR(256),
                        USER_ID VARCHAR(256) NOT NULL,
                        MARKER VARCHAR(256),
                        LOGGER VARCHAR(256) NOT NULL,
                        MESSAGE VARCHAR(2000),
                        THROWABLE VARCHAR(4000)
                    )
                    """;
            statement.execute(sql);

            statement.execute("create sequence if not exists LOGGING_SEQUENCE as bigint start with 1 increment by 1");
            statement.execute("create index if not exists LOGGING_IDX_EVENTDATE on LOGGING (EVENT_DATE)");
            // statement.execute("create index if not exists LOGGING_IDX_MODUL on LOGGING (MODUL)");
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
