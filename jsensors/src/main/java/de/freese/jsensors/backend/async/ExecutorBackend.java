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
    private final Backend delegate;

    private final Executor executor;

    public ExecutorBackend(final Backend delegate, final Executor executor) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    public ExecutorBackend(final Backend delegate, final int parallelism, final ThreadFactory threadFactory) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        Objects.requireNonNull(threadFactory, "threadFactory required");

        this.executor = Executors.newFixedThreadPool(parallelism, threadFactory);
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        this.executor.execute(() -> {
            // final Thread currentThread = Thread.currentThread();
            // String oldName = currentThread.getName();
            // currentThread.setName("task-" + sensorValue.getName());

            try {
                this.delegate.store(sensorValue);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
            //            finally
            //            {
            // currentThread.setName(oldName);
            //            }
        });
    }
}
