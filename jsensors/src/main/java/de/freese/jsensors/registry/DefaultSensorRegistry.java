package de.freese.jsensors.registry;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.sensor.DefaultSensor;
import de.freese.jsensors.sensor.Sensor;

/**
 * Base Implementation of a {@link SensorRegistry}.
 *
 * @author Thomas Freese
 * @since 02.09.2021
 */
class DefaultSensorRegistry implements SensorRegistry {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Sensor> sensors = new TreeMap<>();

    @Override
    public Sensor getSensor(final String name) {
        final Sensor sensor = sensors.get(name);

        if (sensor == null) {
            throw new IllegalStateException(String.format("sensor does not exist: '%s'", name));
        }

        return sensor;
    }

    @Override
    public Stream<Sensor> getSensors() {
        return sensors.values().stream();
    }

    @Override
    public synchronized <T> Sensor registerSensor(final String name, final T obj, final Function<T, String> valueFunction, final String description) {
        if (sensors.containsKey(name)) {
            throw new IllegalStateException(String.format("sensor already exist: '%s'", name));
        }

        final Sensor sensor = new DefaultSensor<>(name, obj, valueFunction, description);

        sensors.put(name, sensor);

        return sensor;
    }

    protected Logger getLogger() {
        return logger;
    }
}
