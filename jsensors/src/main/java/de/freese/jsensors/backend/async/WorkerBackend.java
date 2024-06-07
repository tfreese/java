// Created: 26.04.2019
package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.DefaultSensorValue;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Asynchronous Wrapper for a {@link Backend}.<br>
 * Analog org/apache/logging/log4j/core/appender/AsyncAppenderEventDispatcher.java
 *
 * @author Thomas Freese
 */
public class WorkerBackend extends AbstractBackend implements LifeCycle {
    private static final SensorValue STOP_VALUE = new DefaultSensorValue("STOP_VALUE", "STOP_VALUE", 1);

    /**
     * @author Thomas Freese
     */
    private final class QueueWorker extends Thread {
        @Override
        public void run() {
            while (!stoppedRef.get()) {
                SensorValue sensorValue = null;

                try {
                    sensorValue = WorkerBackend.this.queue.take();
                }
                catch (InterruptedException ex) {
                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                    break;
                }

                if (sensorValue == STOP_VALUE) {
                    break;
                }

                dispatch(sensorValue);
            }

            getLogger().debug("terminated: {}", WorkerBackend.this.getName());
        }
    }

    private final Backend delegateBackend;
    private final BlockingQueue<SensorValue> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean stoppedRef;
    private final QueueWorker worker;

    public WorkerBackend(final Backend delegateBackend) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");

        this.stoppedRef = new AtomicBoolean();

        this.worker = new QueueWorker();
    }

    @Override
    public void start() {
        this.worker.setName(getName());
        this.worker.setDaemon(true);

        worker.start();
    }

    @Override
    public void stop() {
        final boolean stopped = stoppedRef.compareAndSet(false, true);

        if (stopped) {
            getLogger().debug("signaled to stop: {}", getName());
        }

        // There is a slight chance that the thread is not started yet, wait for it to run.
        // Otherwise, interrupt + join might block.
        while (Thread.State.NEW.equals(this.worker.getState())) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            }
            catch (InterruptedException ex) {
                getLogger().error(ex.getMessage(), ex);

                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
        }

        final boolean added = queue.offer(STOP_VALUE);

        if (!added) {
            this.worker.interrupt();
        }

        // Wait for the completion.
        try {
            this.worker.join(200);
        }
        catch (InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        // Save last SensorValues.
        if (!this.queue.isEmpty()) {
            getLogger().info("store queued sensor values");

            SensorValue sensorValue = null;

            while ((sensorValue = this.queue.poll()) != null) {
                if (sensorValue == STOP_VALUE) {
                    continue;
                }

                dispatch(sensorValue);
            }
        }
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        this.queue.add(sensorValue);
    }

    private void dispatch(final SensorValue sensorValue) {
        getLogger().debug("Processing: {}", sensorValue);

        this.delegateBackend.store(sensorValue);
    }

    private String getName() {
        return this.delegateBackend.getClass().getSimpleName().replace("Backend", "Worker");
    }
}
