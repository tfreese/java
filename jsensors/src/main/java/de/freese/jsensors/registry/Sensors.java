package de.freese.jsensors.registry;

import java.util.function.Function;
import java.util.stream.Stream;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 * @since 24.09.26
 */
public final class Sensors {
    private static final SensorRegistry GLOBAL_REGISTRY = new DefaultSensorRegistry();

    /**
     * Throws an IllegalStateException if no {@link Sensor} exist for this Name.
     */
    public static Sensor getSensor(final String name) {
        return GLOBAL_REGISTRY.getSensor(name);
    }

    public static Stream<Sensor> getSensors() {
        return GLOBAL_REGISTRY.getSensors();
    }

    /**
     * Register a Sensor.
     */
    public static <T> Sensor registerSensor(final String name, final T obj, final Function<T, String> valueFunction, final String description, final Backend backend) {
        return GLOBAL_REGISTRY.registerSensor(name, obj, valueFunction, description, backend);
    }

    private Sensors() {
        super();
    }
}
