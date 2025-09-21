package de.freese.logging;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        final Path dbPath = Paths.get(System.getProperty("user.home"), "db", "logging", "logging");

        final JDBCPool pool = new JDBCPool(3);
        pool.setUrl("jdbc:hsqldb:file:" + dbPath + ";shutdown=true");
        pool.setUser("sa");
        pool.setPassword("sa");

        // where EVENT_DATE > TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_DATE)
        // where EVENT_DATE > DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)

        dataSource = pool;

        initDbLog4j();
        initDbLogback();
    }

    public void close() throws Exception {
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

    private void initDbLogback() {
        if (existTable("LOGGING_EVENT")) {
            return;
        }

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("ch/qos/logback/classic/db/script/hsqldb.sql");
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(inputStreamReader);
                 Stream<String> lines = reader.lines()) {

                final String sql = lines
                        .filter(Objects::nonNull)
                        .map(String::strip)
                        .filter(line -> !line.isEmpty())
                        .filter(line -> !line.startsWith("#"))
                        .filter(line -> !line.startsWith("--"))
                        .map(line -> line.replace("\n", " ").replace("\r", " "))
                        .collect(Collectors.joining(" "));

                statement.execute(sql);
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
