// Created: 28.06.2018
package de.freese.logging;

import java.net.URL;
import java.nio.file.Paths;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.logging.context.MapInitialContext;

/**
 * @author Thomas Freese
 */
final class LogbackDemo {
    public static void main(final String[] args) throws Exception {
        try {
            MapInitialContext.init();

            Context initialContext = new InitialContext();
            initialContext.bind("java:comp/env/jdbc/logging", ConnectionFactory.getInstance().getDataSource());

            reConfigureLogback("logback/logback-default.xml");
            doLog(LoggerFactory.getLogger(LogbackDemo.class));

            // Programmatic ShutdownHook for Async-Appender, see xml#shutdownHook
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.stop();
        }
        finally {
            ConnectionFactory.getInstance().close();
        }

        System.exit(0);
    }

    private static void doLog(final Logger logger) {
        logger.error("error-msg", new Exception("Test"));

        for (int i = 0; i < 5; i++) {
            logger.info("info-msg");
        }
    }

    /**
     * Re-Initialisation with Log-File.
     */
    private static void reConfigureLogback(final String configFile) throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        JoranConfigurator config = new JoranConfigurator();
        config.setContext(lc);

        //        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = ClassLoader.getSystemResource(configFile);

        if (url == null) {
            url = LogbackDemo.class.getClassLoader().getResource(configFile);
        }

        if (url == null) {
            url = ClassLoader.getSystemResource(configFile);
        }

        if (url == null) {
            url = Paths.get("maven-projects", "de/freese/logging", "src", "main", "java", configFile).toUri().toURL();
        }

        config.doConfigure(url);
    }

    private LogbackDemo() {
        super();
    }

    /**
     * Use DataSource from Spring-Framework.
     */
    //@Bean
    public DBAppender dbAppender(DataSource dataSource) {
        DataSourceConnectionSource connectionSource = new DataSourceConnectionSource();
        connectionSource.setDataSource(dataSource);
        connectionSource.start();

        DBAppender dbAppender = new DBAppender();
        dbAppender.setConnectionSource(connectionSource);
        dbAppender.start();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger("ROOT");
        logger.addAppender(dbAppender);

        return dbAppender;
    }
}
