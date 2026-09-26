package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.DefaultSensorValue;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Asynchronous Wrapper for a {@link Backend}.<br>
 * Analog org/apache/logging/log4j/core/appender/AsyncAppenderEventDispatcher.java
 *
 * @author Thomas Freese
 * @since 26.04.2019
 */
public class WorkerBackend implements Backend, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerBackend.class);

    private static final SensorValue STOP_VALUE = new DefaultSensorValue("STOP_VALUE", "STOP_VALUE", 1);

    /**
     * @author Thomas Freese
     */
    private final class QueueWorker extends Thread {
        @Override
        public void run() {
            while (!stoppedRef.get()) {
                final SensorValue sensorValue;

                try {
                    sensorValue = WorkerBackend.this.queue.take();
                }
                catch (InterruptedException _) {
                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                    break;
                }

                if (sensorValue == STOP_VALUE) {
                    break;
                }

                dispatch(sensorValue);
            }

            LOGGER.debug("terminated: {}", WorkerBackend.this.getName());
        }
    }

    private final Backend delegateBackend;
    private final BlockingQueue<SensorValue> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean stoppedRef;
    private final QueueWorker worker;

    public WorkerBackend(final Backend delegateBackend) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");

        stoppedRef = new AtomicBoolean();
        worker = new QueueWorker();

        worker.setName(getName());
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public void close() {
        final boolean stopped = stoppedRef.compareAndSet(false, true);

        if (stopped) {
            LOGGER.debug("signaled to stop: {}", getName());
        }

        // There is a slight chance that the thread is not started yet, wait for it to run.
        // Otherwise, interrupt + join might block.
        while (Thread.State.NEW.equals(worker.getState())) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            }
            catch (final InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);

                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
        }

        final boolean added = queue.offer(STOP_VALUE);

        if (!added) {
            worker.interrupt();
        }

        // Wait for the completion.
        try {
            worker.join(200L);
        }
        catch (final InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            LOGGER.error(ex.getMessage(), ex);
        }

        // Save last SensorValues.
        if (!queue.isEmpty()) {
            LOGGER.info("store queued sensor values");

            SensorValue sensorValue;

            while ((sensorValue = queue.poll()) != null) {
                if (sensorValue == STOP_VALUE) {
                    continue;
                }

                dispatch(sensorValue);
            }
        }
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

        queue.add(sensorValue);
    }

    private void dispatch(final SensorValue sensorValue) {
        LOGGER.debug("Processing: {}", sensorValue);

        delegateBackend.store(sensorValue);
    }

    private String getName() {
        return delegateBackend.getClass().getSimpleName().replace("Backend", "Worker");
    }
}
