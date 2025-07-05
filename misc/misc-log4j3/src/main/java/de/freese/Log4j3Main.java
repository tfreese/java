// Created: 02 Mai 2025
package de.freese;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class Log4j3Main {
    public static void main(final String[] args) throws Exception {
        try {
            System.setProperty("log4j.jndi.enableJdbc", "true");

            MapInitialContext.init();

            final Context initialContext = new InitialContext();
            initialContext.bind("java:comp/env/jdbc/logging", ConnectionFactory.getInstance().getDataSource());

            final Logger logger = LoggerFactory.getLogger(Log4j3Main.class);

            logger.error("error-msg", new Exception("Test"));

            for (int i = 0; i < 5; i++) {
                logger.info("info-msg-{}", i);
            }

            // Programmatic ShutdownHook for Async-Appender, see xml#shutdownHook
            // For this 'shutdownHook="disable"' must configure in the XML!
            LogManager.shutdown();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            ConnectionFactory.getInstance().close();
        }
    }

    private Log4j3Main() {
        super();
    }
}
