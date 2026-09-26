package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Base Implementation for a {@link Backend} with Batching.
 *
 * @author Thomas Freese
 * @since 09.11.2020
 */
public abstract class AbstractBatchBackend implements Backend, AutoCloseable {
    private final int batchSize;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<SensorValue> buffer = new ArrayList<>();

    protected AbstractBatchBackend(final int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize < 1: " + batchSize);
        }

        super();

        this.batchSize = batchSize;
    }

    @Override
    public void close() {
        submit();
    }

    @Override
    public final void store(final SensorValue sensorValue) {
        if (sensorValue == null) {
            getLogger().warn("sensorValue is null");
            return;
        }

        if (sensorValue.value() == null || sensorValue.value().isEmpty()) {
            getLogger().warn("sensorValue without content");
            return;
        }

        getLogger().debug("{}", sensorValue);

        try {
            addValue(sensorValue);
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    public void submit() {
        storeValues(flush());
    }

    protected void addValue(final SensorValue sensorValue) {
        buffer.add(sensorValue);

        if (buffer.size() >= getBatchSize()) {
            submit();
        }
    }

    protected List<SensorValue> flush() {
        final List<SensorValue> list = buffer;
        buffer = new ArrayList<>();

        return list;
    }

    protected int getBatchSize() {
        return batchSize;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected abstract void storeValues(List<SensorValue> values);
}
