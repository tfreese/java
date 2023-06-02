// Created: 03.09.2021
package de.freese.jsensors.registry;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.CompositeBackend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Implementation of a {@link SensorRegistry} to schedule the value determination for a {@link Sensor} independent of each other.<br>
 * The Result of each {@link Sensor} is delegated to a {@link Backend}.<br>
 * Use {@link CompositeBackend} for multiple {@link Backend}s for one {@link Sensor}.
 *
 * @author Thomas Freese
 */
public class ScheduledSensorRegistry extends AbstractSensorRegistry implements LifeCycle {
    private final int corePoolSize;

    private final ThreadFactory threadFactory;

    private ScheduledExecutorService scheduledExecutorService;

    public ScheduledSensorRegistry(final ThreadFactory threadFactory, final int corePoolSize) {
        super();

        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory required");

        if (corePoolSize < 1) {
            throw new IllegalArgumentException("corePoolSize < 1: " + corePoolSize);
        }

        this.corePoolSize = corePoolSize;
    }

    /**
     * Schedules the determination for a {@link SensorValue} of a {@link Sensor}.<br>
     * The {@link SensorValue} is passed to a {@link Backend}.<br>
     */
    public void scheduleSensor(final String name, final long initialDelay, final long delay, final TimeUnit unit) {
        if (this.scheduledExecutorService == null) {
            throw new IllegalStateException("scheduler is not started: call #start() first");
        }

        Sensor sensor = getSensor(name);

        Runnable task = () -> getBackend(sensor.getName()).store(sensor.measure());

        this.scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    @Override
    public void start() {
        if (this.scheduledExecutorService != null) {
            stop();
        }

        this.scheduledExecutorService = Executors.newScheduledThreadPool(this.corePoolSize, this.threadFactory);
    }

    @Override
    public void stop() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
            this.scheduledExecutorService = null;
        }
    }
}
