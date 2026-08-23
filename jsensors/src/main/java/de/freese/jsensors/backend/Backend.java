package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 * @since 12.05.2017
 */
@FunctionalInterface
public interface Backend {
    void store(SensorValue sensorValue);
}
