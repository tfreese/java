// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.util.Objects;

/**
 * Default Implementation for a {@link SensorValue}.
 *
 * @author Thomas Freese
 */
public record DefaultSensorValue(String name, String value, long timestamp) implements SensorValue {
    public DefaultSensorValue(final String name, final String value, final long timestamp) {
        this.name = Objects.requireNonNull(name, "name required");
        this.value = Objects.requireNonNull(value, "value required");

        if (timestamp < 1) {
            throw new IllegalArgumentException("timestamp < 1: " + timestamp);
        }

        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensorValue ["
                + "name=" + name()
                + ", value=" + value
                + ", timestamp=" + timestamp()
                + ", date=" + getLocalDateTime()
                + "]";
    }
}
