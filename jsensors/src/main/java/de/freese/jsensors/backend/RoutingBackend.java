// Created: 04.09.2021
package de.freese.jsensors.backend;

import java.util.HashMap;
import java.util.Map;

import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Routes a {@link SensorValue} to multiple {@link Backend}s for a {@link Sensor}.
 *
 * @author Thomas Freese
 */
public class RoutingBackend extends AbstractBackend {
    private final Map<String, CompositeBackend> routes = new HashMap<>();

    public RoutingBackend route(final String sensorName, final Backend backend) {
        this.routes.computeIfAbsent(sensorName, key -> new CompositeBackend()).add(backend);

        return this;
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        CompositeBackend compositeBackend = this.routes.get(sensorValue.getName());

        if (compositeBackend == null) {
            throw new IllegalStateException(String.format("no backends for sensor: '%s'", sensorValue.getName()));
        }

        compositeBackend.store(sensorValue);
    }
}
