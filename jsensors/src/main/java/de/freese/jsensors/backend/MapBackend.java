// Created: 01.06.23
package de.freese.jsensors.backend;

import java.util.HashMap;
import java.util.Map;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
public final class MapBackend extends AbstractBackend {

    private final Map<String, SensorValue> map = new HashMap<>();

    public SensorValue getValue(String name) {
        return this.map.get(name);
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        map.put(sensorValue.getName(), sensorValue);
    }
}
