// Created: 04.03.2021
package de.freese.simulationen;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class SimulationEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationEnvironment.class);

    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SimulationEnvironmentHolder {
        private static final SimulationEnvironment INSTANCE = new SimulationEnvironment();

        private SimulationEnvironmentHolder() {
            super();
        }
    }

    public static SimulationEnvironment getInstance() {
        return SimulationEnvironmentHolder.INSTANCE;
    }

    public static void shutdown(final ExecutorService executorService, final Logger logger) {
        logger.info("shutdown ExecutorService");

        if (executorService == null) {
            logger.warn("ExecutorService is null");

            return;
        }

        executorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Timed out while waiting for ExecutorService");

                // Cancel currently executing tasks.
                executorService.shutdownNow().stream()
                        .filter(Future.class::isInstance)
                        .map(Future.class::cast)
                        .forEach(future -> future.cancel(true))
                ;

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("ExecutorService did not terminate");
                }
                else {
                    logger.info("ExecutorService terminated");
                }
            }
            else {
                logger.info("ExecutorService terminated");
            }
        }
        catch (InterruptedException iex) {
            logger.warn("Interrupted while waiting for ExecutorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }

    private final Properties properties = new Properties();

    private ScheduledExecutorService scheduledExecutorService;

    public boolean getAsBoolean(final String property, final boolean nullDefault) {
        final String value = properties.getProperty(property);

        return value != null ? Boolean.parseBoolean(value) : nullDefault;
    }

    public int getAsInt(final String property, final int nullDefault) {
        final String value = properties.getProperty(property);

        return value != null ? Integer.parseInt(value) : nullDefault;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void init() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("simulation.properties")) {
            properties.load(inputStream);
        }

        scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void shutdown() {
        shutdown(scheduledExecutorService, LOGGER);
    }
}
