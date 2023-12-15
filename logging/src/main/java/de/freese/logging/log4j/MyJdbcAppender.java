// Created: 17.06.2018
package de.freese.logging.log4j;

import java.io.Flushable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.util.Strings;

/**
 * Simplified JdbcAppender.<br>
 * Own PlugIns must be defined in the log4j2.xml within 'Configuration packages="de.freese.logging.log4j"'.
 *
 * @author Thomas Freese
 */
@Plugin(name = "MyJdbc", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class MyJdbcAppender extends AbstractAppender implements Flushable {
    /**
     * @author Thomas Freese
     */
    public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<MyJdbcAppender> {
        @PluginBuilderAttribute
        private int bufferSize = 0;

        @PluginElement("ColumnConfigs")
        private ColumnConfig[] columnConfigs;

        @PluginElement("ConnectionSource")
        @Required(message = "No ConnectionSource provided")
        private ConnectionSource connectionSource;

        @PluginBuilderAttribute
        @Required(message = "No table name provided")
        private String tableName;

        @Override
        public MyJdbcAppender build() {
            if ((this.columnConfigs == null) || (this.columnConfigs.length == 0)) {
                AbstractLifeCycle.LOGGER.error("Cannot create JdbcAppender without any columns.");
                return null;
            }

            return new MyJdbcAppender(getName(), this.tableName, this.bufferSize, this.connectionSource, this.columnConfigs);
            // return new JdbcAppender(getName(), this.tableName, this.bufferSize, this.connectionSource, this.columnConfigs, getConfiguration());
        }

        /**
         * If an integer greater than 0, this causes the appender to buffer log events and flush whenever the buffer reaches this size.
         */
        public B setBufferSize(final int bufferSize) {
            this.bufferSize = bufferSize;

            return asBuilder();
        }

        public B setColumnConfigs(final ColumnConfig... columnConfigs) {
            this.columnConfigs = columnConfigs;

            return asBuilder();
        }

        /**
         * The connections source from which database connections should be retrieved.
         */
        public B setConnectionSource(final ConnectionSource connectionSource) {
            this.connectionSource = connectionSource;

            return asBuilder();
        }

        /**
         * The name of the database table to insert log events into.
         */
        public B setTableName(final String tableName) {
            this.tableName = tableName;

            return asBuilder();
        }
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    private final List<LogEvent> buffer;
    private final int bufferSize;
    private final ColumnConfig[] columnConfigs;
    private final ConnectionSource connectionSource;
    private final Lock lock = new ReentrantLock();
    private final String tableName;

    private Connection connection;
    private boolean isBatchSupported = true;
    private String sql;
    private PreparedStatement statement;

    public MyJdbcAppender(final String name, final String tableName, final int bufferSize, final ConnectionSource connectionSource, final ColumnConfig[] columnConfigs) {
        super(name, null, null, false, Property.EMPTY_ARRAY);

        this.tableName = Objects.requireNonNull(tableName, "tableName required");
        this.bufferSize = bufferSize;
        this.connectionSource = Objects.requireNonNull(connectionSource, "connectionSource required");
        this.columnConfigs = Objects.requireNonNull(columnConfigs, "columnConfigs required");
        this.buffer = new ArrayList<>(bufferSize + 1);
    }

    @Override
    public void append(final LogEvent event) {
        this.lock.lock();

        try {
            if (this.bufferSize > 0) {
                this.buffer.add(event.toImmutable());

                if ((this.buffer.size() >= this.bufferSize) || event.isEndOfBatch()) {
                    flush();
                }
            }
            else {
                connectAndStart();

                try {
                    insert(event.toImmutable());
                }
                finally {
                    commitAndClose();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void flush() {
        if (!this.buffer.isEmpty()) {
            connectAndStart();

            try {
                for (final LogEvent event : this.buffer) {
                    insert(event);
                }
            }
            finally {
                commitAndClose();

                this.buffer.clear();
            }
        }
    }

    @Override
    public void start() {
        try {
            this.connection = this.connectionSource.getConnection();
            final DatabaseMetaData metaData = this.connection.getMetaData();
            this.isBatchSupported = metaData.supportsBatchUpdates();

            if (!this.isBatchSupported) {
                AbstractLifeCycle.LOGGER.warn("No BatchExecution supported for {}", getName());
            }

            Closer.closeSilently(this.connection);
        }
        catch (SQLException sex) {
            AbstractLifeCycle.LOGGER.error(sex.getMessage(), sex);
        }

        setupSql();

        super.start();
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();
        final boolean stopped = super.stop(timeout, timeUnit, false);

        if ((this.connection != null) || (this.statement != null)) {
            commitAndClose();
        }

        if (this.connectionSource != null) {
            this.connectionSource.stop();
        }

        setStopped();

        return stopped;
    }

    /**
     * Commits any active transaction (if applicable) and disconnects from the database (returns the connection to the connection pool). With buffering enabled,
     * this is called when flushing the buffer completes, after the last call to {@link #insert(LogEvent)}. With buffering disabled, this is called immediately
     * after every invocation of {@link #insert(LogEvent)}.
     */
    protected void commitAndClose() {
        try {
            if ((this.connection != null) && !this.connection.isClosed()) {
                if (this.isBatchSupported) {
                    AbstractLifeCycle.LOGGER.debug("Executing batch PreparedStatement {}", this.statement);
                    this.statement.executeBatch();
                }

                AbstractLifeCycle.LOGGER.debug("Committing Connection {}", this.connection);
                this.connection.commit();
            }
        }
        catch (final SQLException sex) {
            throw new AppenderLoggingException("Failed to commit transaction logging event or flushing buffer.", sex);
        }
        finally {
            try {
                AbstractLifeCycle.LOGGER.debug("Closing PreparedStatement {}", this.statement);
                Closer.close(this.statement);
            }
            catch (final Exception ex) {
                AbstractLifeCycle.LOGGER.warn("Failed to close SQL statement logging event or flushing buffer", ex);
            }
            finally {
                this.statement = null;
            }

            try {
                AbstractLifeCycle.LOGGER.debug("Closing Connection {}", this.connection);
                Closer.close(this.connection);
            }
            catch (final Exception ex) {
                AbstractLifeCycle.LOGGER.warn("Failed to close database connection logging event or flushing buffer", ex);
            }
            finally {
                this.connection = null;
            }
        }
    }

    /**
     * Connects to the database and starts a transaction (if applicable). With buffering enabled, this is called when flushing the buffer begins, before the
     * first call to {@link #insert(LogEvent)}. With buffering disabled, this is called immediately before every invocation of {@link #insert(LogEvent)}.
     */
    protected void connectAndStart() {
        try {
            this.connection = this.connectionSource.getConnection();
            this.connection.setAutoCommit(false);

            AbstractLifeCycle.LOGGER.debug("Preparing SQL: {}", this.sql);

            this.statement = this.connection.prepareStatement(this.sql);
        }
        catch (final SQLException sex) {
            throw new AppenderLoggingException("Cannot write logging event or flush buffer; JDBC manager cannot connect to the database.", sex);
        }
    }

    protected void insert(final LogEvent event) {
        StringReader reader = null;

        try {
            if ((this.connection == null) || this.connection.isClosed() || (this.statement == null) || this.statement.isClosed()) {
                throw new AppenderLoggingException("Cannot write logging event; JDBC manager not connected to the database");
            }

            for (int i = 0; i < this.columnConfigs.length; i++) {
                final ColumnConfig columnConfig = this.columnConfigs[i];

                if (columnConfig.isEventTimestamp()) {
                    this.statement.setTimestamp(i + 1, new Timestamp(event.getTimeMillis()));
                }
                else if (columnConfig.isClob()) {
                    reader = new StringReader(columnConfig.getLayout().toSerializable(event));

                    if (columnConfig.isUnicode()) {
                        this.statement.setNClob(i + 1, reader);
                    }
                    else {
                        this.statement.setClob(i + 1, reader);
                    }
                }
                else if (columnConfig.isUnicode()) {
                    this.statement.setNString(i + 1, columnConfig.getLayout().toSerializable(event));
                }
                else {
                    this.statement.setString(i + 1, columnConfig.getLayout().toSerializable(event));
                }
            }

            if (this.isBatchSupported) {
                this.statement.addBatch();
            }
            else if (this.statement.executeUpdate() == 0) {
                throw new AppenderLoggingException("No records inserted in database table for log event in JDBC manager");
            }
        }
        catch (final SQLException sex) {
            throw new AppenderLoggingException("Failed to insert record for log event in JDBC manager: " + sex.getMessage(), sex);
        }
        finally {
            Closer.closeSilently(reader);
        }
    }

    protected void setupSql() {
        final StringJoiner joinerColumns = new StringJoiner(", ", "(", ")");
        final StringJoiner joinerParameter = new StringJoiner(", ", "(", ")");

        for (ColumnConfig columnConfig : this.columnConfigs) {
            final String columnName = columnConfig.getColumnName().toUpperCase();
            joinerColumns.add(columnName);

            if (columnConfig.isEventTimestamp()) {
                AbstractLifeCycle.LOGGER.trace("Adding INSERT VALUE timestamp for ColumnConfig: {} = EventTimestamp ", columnName);
                joinerParameter.add("?");
            }
            else if (Strings.isNotEmpty(columnConfig.getLiteralValue())) {
                AbstractLifeCycle.LOGGER.trace("Adding INSERT VALUE literal for ColumnConfig: {} = {} ", columnName, columnConfig.getLiteralValue());
                joinerParameter.add(columnConfig.getLiteralValue());
            }
            else if (columnConfig.getLayout() != null) {
                AbstractLifeCycle.LOGGER.trace("Adding INSERT VALUE parameter for ColumnConfig: {} = {} ", columnName, columnConfig.getLayout());
                joinerParameter.add("?");
            }
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(this.tableName).append(" ").append(joinerColumns).append(" values ").append(joinerParameter);

        this.sql = sb.toString();
    }
}
