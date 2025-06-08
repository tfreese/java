// Created: 26.04.2019
package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Asynchronous Wrapper for a {@link Backend}.
 *
 * @author Thomas Freese
 */
public class ExecutorBackend extends AbstractBackend {
    private final Backend delegateBackend;
    private final Executor executor;

    public ExecutorBackend(final Backend delegateBackend, final Executor executor) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    public ExecutorBackend(final Backend delegateBackend, final int parallelism, final ThreadFactory threadFactory) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        Objects.requireNonNull(threadFactory, "threadFactory required");

        executor = Executors.newFixedThreadPool(parallelism, threadFactory);
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        executor.execute(() -> {
            // final Thread currentThread = Thread.currentThread();
            // final String oldName = currentThread.getName();
            // currentThread.setName("task-" + sensorValue.getName());

            try {
                delegateBackend.store(sensorValue);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
            // finally {
            //     currentThread.setName(oldName);
            // }
        });
    }
}
