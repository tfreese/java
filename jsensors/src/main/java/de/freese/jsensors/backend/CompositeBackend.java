// Created: 04.09.2021
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Routes a {@link SensorValue} to multiple {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class CompositeBackend extends AbstractBackend
{
    private final List<Backend> backends = new ArrayList<>();

    public CompositeBackend add(final Backend backend)
    {
        this.backends.add(backend);

        return this;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.sensor.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue)
    {
        this.backends.forEach(backend -> backend.store(sensorValue));
    }
}
