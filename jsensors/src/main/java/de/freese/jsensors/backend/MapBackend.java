// Created: 01.06.23
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
public final class MapBackend extends AbstractBackend {
    private final int keepLastNValues;

    private final Map<String, List<SensorValue>> map;

    public MapBackend(final int keepLastNValues) {
        super();

        if (keepLastNValues < 1) {
            throw new IllegalArgumentException("keepLastNValues < 1: " + keepLastNValues);
        }

        this.keepLastNValues = keepLastNValues;
        this.map = new HashMap<>();
    }

    public SensorValue getLastValue(String name) {
        List<SensorValue> values = getValues(name);

        if (values.isEmpty()) {
            return null;
        }

        return values.get(values.size() - 1);
    }

    public List<SensorValue> getValues(String name) {
        return List.copyOf(this.map.computeIfAbsent(name, key -> new ArrayList<>(keepLastNValues)));
    }

    public int size(String name) {
        return getValues(name).size();
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        List<SensorValue> values = map.computeIfAbsent(sensorValue.getName(), key -> new ArrayList<>(keepLastNValues));

        values.add(sensorValue);

        if (values.size() > keepLastNValues) {
            values.remove(0);
        }
    }
}
