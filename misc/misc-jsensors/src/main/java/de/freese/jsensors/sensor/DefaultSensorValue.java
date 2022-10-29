// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.util.Objects;

/**
 * Default Implementation for a {@link SensorValue}.
 *
 * @author Thomas Freese
 */
public class DefaultSensorValue implements SensorValue
{
    private final String name;

    private final long timestamp;

    private final String value;

    public DefaultSensorValue(final String name, final String value, final long timestamp)
    {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.value = Objects.requireNonNull(value, "value required");
        this.timestamp = timestamp;
    }

    /**
     * @see de.freese.jsensors.sensor.SensorValue#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see de.freese.jsensors.sensor.SensorValue#getTimestamp()
     */
    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @see de.freese.jsensors.sensor.SensorValue#getValue()
     */
    @Override
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.jsensors.sensor.SensorValue#getValueAsDouble()
     */
    @Override
    public double getValueAsDouble()
    {
        return Double.parseDouble(getValue());
    }

    /**
     * @see de.freese.jsensors.sensor.SensorValue#getValueAsLong()
     */
    @Override
    public long getValueAsLong()
    {
        return Long.parseLong(getValue());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
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
