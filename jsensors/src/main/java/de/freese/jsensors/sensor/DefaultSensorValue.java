// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.util.Objects;

/**
 * Default Implementation for a {@link SensorValue}.
 *
 * @author Thomas Freese
 */
public class DefaultSensorValue implements SensorValue {
    private final String name;

    private final long timestamp;

    private final String value;

    public DefaultSensorValue(final String name, final String value, final long timestamp) {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.value = Objects.requireNonNull(value, "value required");
        this.timestamp = timestamp;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SensorValue [");
        builder.append("name=").append(getName());
        builder.append(", value=").append(this.value);
        builder.append(", timestamp=").append(getTimestamp());
        builder.append(", date=").append(getLocalDateTime());
        builder.append("]");

        return builder.toString();
    }
}
