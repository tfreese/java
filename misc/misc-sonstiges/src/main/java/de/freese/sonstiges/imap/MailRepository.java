package de.freese.sonstiges.imap;

import java.io.BufferedReader;
import java.io.Closeable;
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

import org.h2.jdbcx.JdbcConnectionPool;
import org.hsqldb.jdbc.JDBCPool;
import org.springframework.beans.factory.DisposableBean;

import de.freese.sonstiges.imap.model.MessageWrapper;
import de.freese.sonstiges.imap.model.Token;

/**
 * @author Thomas Freese
 */
public class MailRepository implements AutoCloseable {
    private static DataSource toDataSource(final Path dbPath) {
        // H2
        JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:file:" + dbPath.resolve("h2"), "sa", "sa");
        pool.setMaxConnections(3);

        // Hsqldb
        // JDBCPool pool = new JDBCPool(3);
        // pool.setUrl("jdbc:hsqldb:file:" + dbPath.resolve("hsqldb") + ";shutdown=true");
        // pool.setUser("sa");
        // pool.setPassword("sa");

        // MariaDbPoolDataSource pool = new MariaDbPoolDataSource("jdbc:mariadb://localhost:3306/testdb?user=root&password=rootpw&maxPoolSize=3");

        // Oracle
        // HikariConfig config = new HikariConfig();
        // config.setDriverClassName("oracle.jdbc.OracleDriver");
        // config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        // config.setUsername("testuser");
        // config.setPassword("testpw");
        // config.setMinimumIdle(1);
        // config.setMaximumPoolSize(3);
        // config.setConnectionTimeout(5 * 1000L); // Sekunden
        // config.addDataSourceProperty("cachePrepStmts", "true");
        // config.addDataSourceProperty("prepStmtCacheSize", "250");
        // config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        //
        // HikariDataSource pool = new HikariDataSource(config);

        return pool;
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
        try (Connection connection = this.dataSource.getConnection()) {
            String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();

            // Wird bei hsql bereits durch 'shutdown=true' erledigt.
            if (dbName.contains("h2")) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("SHUTDOWN COMPACT");
                }
            }
        }
        catch (SQLException ex) {
            // Ignore
        }

        if (this.dataSource instanceof JDBCPool p) {
            p.close(1);
        }
        else if (this.dataSource instanceof JdbcConnectionPool p) {
            p.dispose();
        }
        else if (this.dataSource instanceof AutoCloseable ac) {
            ac.close();
        }
        else if (this.dataSource instanceof Closeable c) {
            c.close();
        }
        else if (this.dataSource instanceof DisposableBean db) {
            db.destroy();
        }

        this.dataSource = null;
    }

    public boolean containsMessageId(final long messageId) throws SQLException {
        String sql = "select count(*) from MESSAGE where MESSAGE_ID = ?";

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setLong(1, messageId);

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                resultSet.next();

                int result = resultSet.getInt(1);

                return result > 0;
            }
        }
    }

    public int countMessagesForFolder(final String folderName) throws SQLException {
        String sql = "select count(*) from MESSAGE where FOLDER_NAME = ?";

        try (Connection connection = this.dataSource.getConnection();
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

        try (Connection connection = this.dataSource.getConnection();
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
             Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // @formatter:off
            String sql = bufferedReader.lines()
                    .filter(Objects::nonNull)
                    .map(String::strip)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> !line.startsWith("--"))
                    .map(line -> line.replace("\n", " ").replace("\r", " "))
                    .collect(Collectors.joining(" "))
                    ;
            // @formatter:on

            // statement.execute(sql);

            try (Scanner scanner = new Scanner(sql)) {
                scanner.useDelimiter("; ");

                while (scanner.hasNext()) {
                    String s = scanner.next();
                    statement.execute(s);
                }
            }
        }
    }

    public Set<Token> getToken(final Collection<String> values) throws SQLException {
        // @formatter:off
        String inClause = values.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> v.length() > 0)
                .distinct()
                .map(v -> "'" + v + "'")
                .collect(Collectors.joining(",", "(", ")"))
                ;
        // @formatter:on

        if (inClause.length() <= 2) {
            // in ()
            return Collections.emptySet();
        }

        String sql = """
                select * from TOKEN
                    where VALUE in %s
                """.formatted(inClause);

        Set<Token> result = new HashSet<>();

        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String value = resultSet.getString("VALUE");
                int hamCount = resultSet.getInt("HAM_COUNT");
                int spamCount = resultSet.getInt("SPAM_COUNT");

                Token token = new Token(value, hamCount, spamCount);
                result.add(token);
            }
        }

        return result;
    }

    public void insertMessage(final MessageWrapper messageWrapper) throws Exception {
        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            String sql = """
                    insert into MESSAGE (MESSAGE_ID, FOLDER_NAME, SUBJECT, IS_SPAM, RECEIVED_DATE, SENDER)
                        values (?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement preparedStatementMessage = connection.prepareStatement(sql)) {
                preparedStatementMessage.setLong(1, messageWrapper.getMessageId());
                preparedStatementMessage.setString(2, messageWrapper.getFolderName());
                preparedStatementMessage.setString(3, messageWrapper.getSubject());
                preparedStatementMessage.setBoolean(4, messageWrapper.isSpam());
                preparedStatementMessage.setTimestamp(5, new java.sql.Timestamp(messageWrapper.getReceivedDate().getTime()));
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
        Map<String, Token> existingToken = getToken(wordCount.keySet()).stream().collect(Collectors.toMap(Token::getValue, Function.identity()));

        String sqlTokenInsert = "insert into TOKEN (VALUE, HAM_COUNT, SPAM_COUNT) values (?, ?, ?)";
        String sqlTokenUpdate = "update TOKEN set HAM_COUNT = ?, SPAM_COUNT = ? where VALUE = ?";

        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstTokenInsert = connection.prepareStatement(sqlTokenInsert);
                 PreparedStatement pstTokenUpdate = connection.prepareStatement(sqlTokenUpdate)) {
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    String value = entry.getKey();
                    Token token = existingToken.get(value);

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

        String sqlMessageToken = "insert into MESSAGE_TOKEN (MESSAGE_ID, VALUE, COUNT) values (?, ?, ?)";

        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatementMessageToken = connection.prepareStatement(sqlMessageToken)) {
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    String token = entry.getKey();
                    int count = entry.getValue();

                    // Message_Token
                    preparedStatementMessageToken.setLong(1, messageWrapper.getMessageId());
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
