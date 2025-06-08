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
public final class CompositeBackend extends AbstractBackend {
    private final List<Backend> backends = new ArrayList<>();

    public CompositeBackend add(final Backend backend) {
        if (!backends.contains(backend)) {
            backends.add(backend);
        }

        return this;
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        for (Backend backend : backends) {
            try {
                backend.store(sensorValue);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }
    }
}
