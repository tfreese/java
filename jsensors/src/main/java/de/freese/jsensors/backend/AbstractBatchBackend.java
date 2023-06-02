// Created: 09.11.2020
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Base Implementation for a {@link Backend} with Batching.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBatchBackend extends AbstractBackend {
    private final int batchSize;

    private List<SensorValue> buffer;

    protected AbstractBatchBackend(final int batchSize) {
        super();

        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize < 1: " + batchSize);
        }

        this.batchSize = batchSize;
    }

    public void submit() {
        storeValues(flush());
    }

    protected List<SensorValue> flush() {
        List<SensorValue> list = this.buffer;
        this.buffer = null;

        return list;
    }

    protected int getBatchSize() {
        return this.batchSize;
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        if (this.buffer == null) {
            this.buffer = new ArrayList<>();
        }

        this.buffer.add(sensorValue);

        if (this.buffer.size() >= getBatchSize()) {
            submit();
        }
    }

    protected abstract void storeValues(List<SensorValue> values);
}
