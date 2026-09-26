package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;

import de.freese.jsensors.sensor.SensorValue;

/**
 * Routes a {@link SensorValue} to multiple {@link Backend}s.
 *
 * @author Thomas Freese
 * @since 04.09.2021
 */
public final class CompositeBackend implements Backend {
    private final List<Backend> backends = new ArrayList<>();

    public CompositeBackend add(final Backend backend) {
        if (!backends.contains(backend)) {
            backends.add(backend);
        }

        return this;
    }

    @Override
    public void store(final SensorValue sensorValue) {
        backends.forEach(backend -> backend.store(sensorValue));
    }
}
