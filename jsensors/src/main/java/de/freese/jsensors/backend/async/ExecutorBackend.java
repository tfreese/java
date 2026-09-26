package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Asynchronous Wrapper for a {@link Backend}.
 *
 * @author Thomas Freese
 * @since 26.04.2019
 */
public class ExecutorBackend implements Backend {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorBackend.class);

    private final Backend delegateBackend;
    private final Executor executor;

    public ExecutorBackend(final Backend delegateBackend, final Executor executor) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    public ExecutorBackend(final Backend delegateBackend, final int parallelism, final ThreadFactory threadFactory) {
        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        Objects.requireNonNull(threadFactory, "threadFactory required");

        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");

        executor = Executors.newFixedThreadPool(parallelism, threadFactory);
    }

    @Override
    public void store(final SensorValue sensorValue) {
        if (sensorValue == null) {
            LOGGER.warn("sensorValue is null");
            return;
        }

        if (sensorValue.value() == null || sensorValue.value().isEmpty()) {
            LOGGER.warn("sensorValue without content");
            return;
        }

        executor.execute(() -> {
            // final Thread currentThread = Thread.currentThread();
            // final String oldName = currentThread.getName();
            // currentThread.setName("task-" + sensorValue.getName());

            try {
                delegateBackend.store(sensorValue);
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            // finally {
            //     currentThread.setName(oldName);
            // }
        });
    }
}
