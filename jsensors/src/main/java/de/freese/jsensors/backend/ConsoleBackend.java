// Created: 27.10.2020
package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Prints the {@link SensorValue} to System.out().
 *
 * @author Thomas Freese
 */
public final class ConsoleBackend extends AbstractBackend {
    @Override
    protected void storeValue(final SensorValue sensorValue) {
        System.out.printf("[%s] - %s%n", Thread.currentThread().getName(), sensorValue);
    }
}
