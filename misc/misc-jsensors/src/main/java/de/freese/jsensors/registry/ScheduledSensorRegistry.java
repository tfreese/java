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
public class ScheduledSensorRegistry extends AbstractSensorRegistry implements LifeCycle
{
    /**
     *
     */
    private final int corePoolSize;
    /**
     *
     */
    private final ThreadFactory threadFactory;
    /**
     *
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Erstellt ein neues {@link ScheduledSensorRegistry} Object.
     *
     * @param threadFactory {@link ThreadFactory}
     * @param corePoolSize int
     */
    public ScheduledSensorRegistry(final ThreadFactory threadFactory, final int corePoolSize)
    {
        super();

        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory required");

        if (corePoolSize < 1)
        {
            throw new IllegalArgumentException("corePoolSize < 1: " + corePoolSize);
        }

        this.corePoolSize = corePoolSize;
    }

    /**
     * Schedules the determination for a {@link SensorValue} of a {@link Sensor}.<br>
     * The {@link SensorValue} can accessed by: <code>SensorRegistry.getSensor(String).getValueLast()</code>.
     *
     * @param name String
     * @param initialDelay long
     * @param delay long
     * @param unit {@link TimeUnit}
     */
    public void scheduleSensor(final String name, final long initialDelay, final long delay, final TimeUnit unit)
    {
        scheduleSensor(name, initialDelay, delay, unit, sensorValue ->
        {
        });
    }

    /**
     * Schedules the determination for a {@link SensorValue} of a {@link Sensor}.<br>
     * The {@link SensorValue} is passed to a {@link Backend}.<br>
     * Use {@link CompositeBackend} for multiple {@link Backend}s for one {@link Sensor}.
     *
     * @param name String
     * @param initialDelay long
     * @param delay long
     * @param unit {@link TimeUnit}
     * @param backend {@link Backend}
     */
    public void scheduleSensor(final String name, final long initialDelay, final long delay, final TimeUnit unit, final Backend backend)
    {
        if (this.scheduledExecutorService == null)
        {
            throw new IllegalStateException("scheduler is not started: call #start() first");
        }

        Objects.requireNonNull(backend, "backend required");

        Sensor sensor = getSensor(name);

        this.scheduledExecutorService.scheduleWithFixedDelay(() -> backend.store(sensor.measure()), initialDelay, delay, unit);
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (this.scheduledExecutorService != null)
        {
            stop();
        }

        this.scheduledExecutorService = Executors.newScheduledThreadPool(this.corePoolSize, this.threadFactory);
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        if (this.scheduledExecutorService != null)
        {
            this.scheduledExecutorService.shutdown();
            this.scheduledExecutorService = null;
        }
    }
}
