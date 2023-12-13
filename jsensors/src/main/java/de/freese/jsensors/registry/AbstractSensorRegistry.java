// Created: 02.09.2021
package de.freese.jsensors.registry;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.NoOpBackend;
import de.freese.jsensors.sensor.DefaultSensor;
import de.freese.jsensors.sensor.Sensor;

/**
 * Base Implementation of a {@link SensorRegistry}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensorRegistry implements SensorRegistry {
    private final Map<String, Backend> backends = new TreeMap<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Sensor> sensors = new TreeMap<>();

    @Override
    public Sensor getSensor(final String name) {
        Sensor sensor = this.sensors.get(name);

        if (sensor == null) {
            throw new IllegalStateException(String.format("sensor does not exist: '%s'", name));
        }

        return sensor;
    }

    @Override
    public Stream<Sensor> getSensors() {
        return this.sensors.values().stream();
    }

    @Override
    public <T> Sensor registerSensor(final String name, final T obj, final Function<T, String> valueFunction, final String description, final Backend backend) {
        if (this.sensors.containsKey(name)) {
            throw new IllegalStateException(String.format("sensor already exist: '%s'", name));
        }

        Objects.requireNonNull(backend, "backend required");

        registerBackend(name, backend);

        return this.sensors.computeIfAbsent(name, key -> new DefaultSensor<>(name, obj, valueFunction, description));
    }

    protected Backend getBackend(final String name) {
        Backend backend = this.backends.computeIfAbsent(name, key -> NoOpBackend.getInstance());

        if (backend instanceof NoOpBackend) {
            getLogger().warn("NoOpBackend is used for sensor: {}", name);
        }

        return backend;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected void registerBackend(final String name, final Backend backend) {
        if (backend instanceof NoOpBackend) {
            getLogger().warn("NoOpBackend is used for sensor: {}", name);
        }

        this.backends.put(name, backend);
    }
}
