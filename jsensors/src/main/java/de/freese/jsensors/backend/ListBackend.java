// Created: 01.06.23
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
public final class ListBackend extends AbstractBackend {

    private final int keepLastNValues;

    private final List<SensorValue> values;

    public ListBackend(int keepLastNValues) {
        super();

        if (keepLastNValues < 1) {
            throw new IllegalArgumentException("keepLastNValues < 1: " + keepLastNValues);
        }

        this.values = new ArrayList<>(keepLastNValues + 1);
        this.keepLastNValues = keepLastNValues;
    }

    public SensorValue getValueLast() {
        if (this.values.isEmpty()) {
            return null;
        }

        return this.values.get(this.values.size() - 1);
    }

    public List<SensorValue> getValues() {
        return List.copyOf(this.values);
    }

    public int size() {
        return this.values.size();
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        this.values.add(sensorValue);

        if (this.values.size() > keepLastNValues) {
            this.values.remove(0);
        }
    }
}
