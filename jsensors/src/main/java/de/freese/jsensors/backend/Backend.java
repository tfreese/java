// Created: 12.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Backend {
    void store(SensorValue sensorValue);
}
