package de.freese.jsensors.backend;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Routes a {@link SensorValue} to multiple {@link Backend}s for a {@link Sensor}.
 *
 * @author Thomas Freese
 * @since 04.09.2021
 */
public class RoutingBackend implements Backend {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingBackend.class);

    private final Map<String, CompositeBackend> routes = new HashMap<>();

    /**
     * Multiple {@link Backend}s for one Sensor possible.
     */
    public RoutingBackend route(final String sensorName, final Backend backend) {
        routes.computeIfAbsent(sensorName, key -> new CompositeBackend()).add(backend);

        return this;
    }

    @Override
    public void store(final SensorValue sensorValue) {
        if (sensorValue == null) {
            LOGGER.warn("sensorValue is null");
            return;
        }

        if (sensorValue.value() == null || sensorValue.value().isEmpty()) {
            LOGGER.warn("sensorValue without content");
            return;
        }

        final CompositeBackend compositeBackend = routes.get(sensorValue.name());

        if (compositeBackend == null) {
            throw new IllegalStateException(String.format("no backends for sensor: '%s'", sensorValue.name()));
        }

        compositeBackend.store(sensorValue);
    }
}
