// Created: 29.10.2020
package de.freese.jsensors.backend.disruptor;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
class SensorEvent {
    private SensorValue sensorValue;

    public SensorValue getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(final SensorValue sensorValue) {
        this.sensorValue = sensorValue;
    }
}
