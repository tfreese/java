// Created: 28.06.2018
package de.freese.logging;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;

import de.freese.logging.context.MapInitialContext;

/**
 * @author Thomas Freese
 */
final class Log4jDemo {
    static void main() throws Exception {
        //        System.getenv().forEach((key, value) -> System.out.printf("%s = %s%n", key, value));
        //        System.getProperties().forEach((key, value) -> System.out.printf("%s = %s%n", key, value));

        //        System.setProperty("HOSTNAME", "localhost");

        System.setProperty("log4j2.enableJndiJdbc", "true");
        // System.setProperty("log4j.jndi.enableLookup", "true"); // log4j3
        // System.setProperty("log4j.jndi.enableJdbc", "true"); // log4j3

        MapInitialContext.init();

        final Context initialContext = new InitialContext();
        initialContext.bind("java:comp/env/jdbc/logging", ConnectionFactory.getInstance().getDataSource());

        try {
            // System.setProperty("log4j2.debug", "true");
            // ThreadContext.put("user", System.getProperty("user.name"));

            // HSQLDB automatic use Log4J as Logging-Backend -> org.hsqldb.lib.FrameworkLogger#reconfigure
            System.setProperty("hsqldb.reconfig_logging", "false");

            // System.setProperty("log4j.configuration.location", "log4j2/log4j2-default.xml"); // log4j3
            System.setProperty("log4j2.configurationFile", "log4j2/log4j2-default.xml");
            // System.setProperty("log4j2.configurationFile", "log4j2/log4j2-default-no-schema.xml");
            doLog(LogManager.getLogger(Log4jDemo.class));

            // ThreadContext.clearAll();

            //        System.out.println();
            //
            //        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
            //        context.getConfiguration().getLoggers().forEach((loggerName, config) -> System.out.println(loggerName));
            //        System.out.println();
            //
            //        context.getLoggers().forEach(System.out::println);
            //        System.out.println();
            //
            //        final Set<String> loggerNames = new TreeSet<>(context.getConfiguration().getLoggers().keySet());
            //        loggerNames.add("Root");
            //        loggerNames.forEach(loggerName -> System.out.println(LogManager.getLogger(loggerName)));
            //        System.out.println();

            // Programmatic ShutdownHook for Async-Appender, see xml#shutdownHook
            // For this 'shutdownHook="disable"' must configure in the XML!
            LogManager.shutdown();
        }
        finally {
            ConnectionFactory.getInstance().close();
        }

        System.exit(0);
    }

    /**
     * Re-Initialisation with Log-File.
     */
    static void reConfigureLog4j(final String configFile) throws URISyntaxException {
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);

        final URL url = ClassLoader.getSystemResource(configFile);

        // this will force a reconfiguration
        context.setConfigLocation(url.toURI());
    }

    private static void doLog(final Logger logger) {
        logger.error("error-msg", new Exception("Test"));

        for (int i = 0; i < 5; i++) {
            logger.info("info-msg-{}", i);
        }
    }

    private Log4jDemo() {
        super();
    }

    /**
     * Use DataSource from Spring-Framework.
     */
    //@Bean
    public JdbcAppender jdbcAppender(final DataSource dataSource) {
        final ConnectionSource connectionSource = new ConnectionSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return dataSource.getConnection();
            }

            @Override
            public State getState() {
                return State.STARTED;
            }

            @Override
            public void initialize() {
                // Empty
            }

            @Override
            public boolean isStarted() {
                return true;
            }

            @Override
            public boolean isStopped() {
                return false;
            }

            @Override
            public void start() {
                // Empty
            }

            @Override
            public void stop() {
                // Empty
            }
        };

        final JdbcAppender jdbcAppender = JdbcAppender.newBuilder()
                .setName("DATABASE_APPENDER")
                .setBufferSize(3)
                .setTableName("LOGGING")
                .setTruncateStrings(true)
                .setConnectionSource(connectionSource)
                .setColumnConfigs(new ColumnConfig.Builder().setName("ID").setLiteral("next value for LOGGING_SEQUENCE").build(),
                        new ColumnConfig.Builder().setName("EVENT_DATE").setEventTimestamp(true).build(),
                        new ColumnConfig.Builder().setName("HOST").setPattern("${sys:HOSTNAME:-localhost}").build(),
                        new ColumnConfig.Builder().setName("MODUL").setPattern("%X{modul}").build(),
                        new ColumnConfig.Builder().setName("LEVEL").setPattern("%level").build(),
                        new ColumnConfig.Builder().setName("THREAD").setPattern("%thread").build(),
                        new ColumnConfig.Builder().setName("USER_ID").setPattern("%equals{%X{USERID}}{}{SYSTEM}").build(),
                        new ColumnConfig.Builder().setName("MARKER").setPattern("%marker").build(),
                        new ColumnConfig.Builder().setName("LOGGER").setPattern("%logger").build(),
                        new ColumnConfig.Builder().setName("MESSAGE").setPattern("%maxLen{%message}{1997}").build(),
                        new ColumnConfig.Builder().setName("THROWABLE").setPattern("%maxLen{%exception{full}}{3997}").build()
                )
                .build();

        jdbcAppender.start();

        final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.getConfiguration().addAppender(jdbcAppender);
        loggerContext.getRootLogger().addAppender(loggerContext.getConfiguration().getAppender(jdbcAppender.getName()));
        loggerContext.updateLoggers();

        return jdbcAppender;
    }
}
