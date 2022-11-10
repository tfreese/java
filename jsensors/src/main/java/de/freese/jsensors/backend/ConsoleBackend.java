// Created: 27.10.2020
package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Prints the {@link SensorValue} to System.out().
 *
 * @author Thomas Freese
 */
public class ConsoleBackend extends AbstractBackend
{
    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.sensor.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue)
    {
        System.out.printf("[%s] - %s%n", Thread.currentThread().getName(), sensorValue);
    }
}
