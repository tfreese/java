// Created: 03.09.2021
package de.freese.jsensors.registry;

import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Default Implementation of a {@link SensorRegistry} to determine all {@link SensorValue} for all {@link Sensor} by one {@link #measureAll()}-Method.
 *
 * @author Thomas Freese
 */
public class DefaultSensorRegistry extends AbstractSensorRegistry {
    /**
     * Determine the next values for all sensors.
     */
    public void measureAll() {
        getSensors().forEach(sensor -> {
            try {
                getBackend(sensor.getName()).store(sensor.measure());
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        });
    }
}
