package de.freese.sonstiges.imap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.DisposableBean;

import de.freese.sonstiges.imap.model.MessageWrapper;
import de.freese.sonstiges.imap.model.Token;

/**
 * @author Thomas Freese
 */
@SuppressWarnings({"try", "java:S6909", "java:S6437"})
public class MailRepository implements AutoCloseable {

    private static DataSource toDataSource(final Path dbPath) {
        // H2
        // final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:file:" + dbPath.resolve("h2"), "...", "...");
        // pool.setMaxConnections(3);

        // Hsqldb
        // final JDBCPool pool = new JDBCPool(3);
        // pool.setUrl("jdbc:hsqldb:file:" + dbPath.resolve("hsqldb") + ";shutdown=true");
        // pool.setUser("...");
        // pool.setPassword("...");

        // final MariaDbPoolDataSource pool = new MariaDbPoolDataSource("jdbc:mariadb://localhost:3306/testdb?user=...&password=...&maxPoolSize=3");

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:file:" + dbPath.resolve("hsqldb") + ";shutdown=true");
        // hikariConfig.setDriverClassName("org.h2.Driver");
        // hikariConfig.setJdbcUrl("jdbc:h2:file:" + dbPath.resolve("h2"));
        // hikariConfig.setDriverClassName("oracle.jdbc.OracleDriver");
        // hikariConfig.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        // hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        // hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/testdb");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(3);
        hikariConfig.setConnectionTimeout(5 * 1000L); // Seconds
        // hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        // hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        // hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(hikariConfig);
    }

    private DataSource dataSource;

    public MailRepository(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public MailRepository(final Path dbPath) {
        this(toDataSource(dbPath));
    }

    @Override
    public void close() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            final String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();

            // Wird bei hsql bereits durch 'shutdown=true' erledigt.
            if (dbName.contains("h2") || dbName.contains("hsql")) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("SHUTDOWN COMPACT");
                }
            }
        }
        catch (SQLException _) {
            // Ignore
        }

        // if (dataSource instanceof JDBCPool p) {
        //     p.close(1);
        // }
        // else if (dataSource instanceof JdbcConnectionPool p) {
        //     p.dispose();
        // }
        // else
        if (dataSource instanceof AutoCloseable ac) {
            ac.close();
        }
        else if (dataSource instanceof DisposableBean db) {
            db.destroy();
        }

        dataSource = null;
    }

    public boolean containsMessageId(final String messageId) throws SQLException {
        final String sql = "select count(*) from MESSAGE where MESSAGE_ID = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setString(1, messageId);

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                resultSet.next();

                final int result = resultSet.getInt(1);

                return result > 0;
            }
        }
    }

    public int countMessagesForFolder(final String folderName) throws SQLException {
        final String sql = "select count(*) from MESSAGE where FOLDER_NAME = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setString(1, folderName);

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                resultSet.next();

                return resultSet.getInt(1);
            }
        }
    }

    public void createDatabaseIfNotExist() throws Exception {
        String dbName = "";
        boolean databaseExists = false;

        try (Connection connection = dataSource.getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "MESSAGE", new String[]{"TABLE"})) {
            dbName = connection.getMetaData().getDatabaseProductName();

            if (resultSet.next()) {
                databaseExists = true;
            }
        }

        if (databaseExists) {
            return;
        }

        dbName = dbName.toLowerCase();

        String schemaSql = "mail_schema.sql";

        if (dbName.contains("mysql") || dbName.contains("mariadb")) {
            schemaSql = "mail_schema_mysql.sql";
        }
        else if (dbName.contains("oracle")) {
            schemaSql = "mail_schema_oracle.sql";
        }

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(schemaSql);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader);
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            final String script = bufferedReader.lines()
                    .filter(Objects::nonNull)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.startsWith("--"))
                    .map(String::strip)
                    .filter(l -> !l.isEmpty())
                    .collect(Collectors.joining(" "));

            // SQLs ending with ';'.
            try (Scanner scanner = new Scanner(script)) {
                scanner.useDelimiter(";");

                while (scanner.hasNext()) {
                    final String sql = scanner.next().strip();
                    // statement.execute(sql);
                    statement.addBatch(sql);
                }

                statement.executeBatch();
            }
        }
    }

    public Set<Token> getToken(final Collection<String> values) throws SQLException {
        final String inClause = values.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isEmpty())
                .distinct()
                .map(v -> "'" + v + "'")
                .collect(Collectors.joining(",", "(", ")"));

        if (inClause.length() <= 2) {
            // in ()
            return Collections.emptySet();
        }

        final String sql = """
                select * from TOKEN
                    where VALUE in %s
                """.formatted(inClause);

        final Set<Token> result = new HashSet<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final String value = resultSet.getString("VALUE");
                final int hamCount = resultSet.getInt("HAM_COUNT");
                final int spamCount = resultSet.getInt("SPAM_COUNT");

                final Token token = new Token(value, hamCount, spamCount);
                result.add(token);
            }
        }

        return result;
    }

    public void insertMessage(final MessageWrapper messageWrapper) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            final String sql = """
                    insert into MESSAGE (MESSAGE_ID, FOLDER_NAME, SUBJECT, IS_SPAM, RECEIVED_DATE, SENDER)
                        values (?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement preparedStatementMessage = connection.prepareStatement(sql)) {
                preparedStatementMessage.setString(1, messageWrapper.getMessageId());
                preparedStatementMessage.setString(2, messageWrapper.getFolderName());
                preparedStatementMessage.setString(3, messageWrapper.getSubject());
                preparedStatementMessage.setBoolean(4, messageWrapper.isSpam());
                preparedStatementMessage.setTimestamp(5, new java.sql.Timestamp(messageWrapper.getDate().getTime()));
                preparedStatementMessage.setString(6, messageWrapper.getFrom());

                preparedStatementMessage.executeUpdate();

                connection.commit();
            }
            catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void insertMessageTokens(final MessageWrapper messageWrapper, final Map<String, Integer> wordCount) throws SQLException {
        // Mysql/Mariadb kennen keinen MERGE.
        // insert into TOKEN (VALUE, HAM_COUNT, SPAM_COUNT) values
        // (?, 0, 1) on duplicate key update SPAM_COUNT = SPAM_COUNT + 1
        // (?, 1, 0) on duplicate key update HAM_COUNT = HAM_COUNT + 1

        // H2
        // merge into TOKEN t using DUAL on VALUE = ?
        //
        // HSQLDB
        // merge into TOKEN t using (values (?)) as s(VALUE)
        // on t.VALUE = s.VALUE
        //
        // when matched then update set t.SPAM_COUNT = t.SPAM_COUNT + 1
        // when not matched then insert (VALUE, HAM_COUNT, SPAM_COUNT) values (?, 0, 1)
        // when matched then update set t.HAM_COUNT = t.HAM_COUNT + 1
        // when not matched then insert (VALUE, HAM_COUNT, SPAM_COUNT) values (?, 1, 0)

        // Vorhandene Token laden.
        final Map<String, Token> existingToken = getToken(wordCount.keySet()).stream().collect(Collectors.toMap(Token::getValue, Function.identity()));

        final String sqlTokenInsert = "insert into TOKEN (VALUE, HAM_COUNT, SPAM_COUNT) values (?, ?, ?)";
        final String sqlTokenUpdate = "update TOKEN set HAM_COUNT = ?, SPAM_COUNT = ? where VALUE = ?";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstTokenInsert = connection.prepareStatement(sqlTokenInsert);
                 PreparedStatement pstTokenUpdate = connection.prepareStatement(sqlTokenUpdate)) {
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    final String value = entry.getKey();
                    final Token token = existingToken.get(value);

                    if (token == null) {
                        // Insert
                        pstTokenInsert.setString(1, value);

                        if (messageWrapper.isSpam()) {
                            pstTokenInsert.setInt(2, 0);
                            pstTokenInsert.setInt(3, 1);
                        }
                        else {
                            pstTokenInsert.setInt(2, 1);
                            pstTokenInsert.setInt(3, 0);
                        }

                        pstTokenInsert.executeUpdate();
                    }
                    else {
                        // Update
                        if (messageWrapper.isSpam()) {
                            pstTokenUpdate.setInt(1, token.getHamCount());
                            pstTokenUpdate.setInt(2, token.getSpamCount() + 1);
                        }
                        else {
                            pstTokenUpdate.setInt(1, token.getHamCount() + 1);
                            pstTokenUpdate.setInt(2, token.getSpamCount());
                        }

                        pstTokenUpdate.setString(3, value);
                        pstTokenUpdate.executeUpdate();
                    }
                }

                connection.commit();
            }
            catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }

        final String sqlMessageToken = "insert into MESSAGE_TOKEN (MESSAGE_ID, VALUE, COUNT) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatementMessageToken = connection.prepareStatement(sqlMessageToken)) {
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    final String token = entry.getKey();
                    final int count = entry.getValue();

                    // Message_Token
                    preparedStatementMessageToken.setString(1, messageWrapper.getMessageId());
                    preparedStatementMessageToken.setString(2, token);
                    preparedStatementMessageToken.setInt(3, count);

                    preparedStatementMessageToken.addBatch();
                    // preparedStatementMessageToken.executeUpdate();
                }

                preparedStatementMessageToken.executeBatch();

                connection.commit();
            }
            catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }
}
