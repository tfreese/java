// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base Implementation for a {@link Sensor}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensor implements Sensor
{
    private final String description;

    private final int keepLastNValues;

    private final String name;

    private final List<SensorValue> values = new ArrayList<>();

    protected AbstractSensor(final String name, final int keepLastNValues, final String description)
    {
        super();

        this.name = Objects.requireNonNull(name, "name required");

        if (keepLastNValues < 1)
        {
            throw new IllegalArgumentException("keepLastNValues < 1: " + keepLastNValues);
        }

        this.keepLastNValues = keepLastNValues;
        this.description = description;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getDescription()
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getValueLast()
     */
    @Override
    public SensorValue getValueLast()
    {
        if (this.values.isEmpty())
        {
            return null;
        }

        return this.values.get(this.values.size() - 1);
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getValues()
     */
    @Override
    public List<SensorValue> getValues()
    {
        return List.copyOf(this.values);
    }

    protected SensorValue addValue(final String value)
    {
        SensorValue sensorValue = new DefaultSensorValue(getName(), value, System.currentTimeMillis());

        this.values.add(sensorValue);

        if (this.values.size() > getKeepLastNValues())
        {
            this.values.remove(0);
        }

        return sensorValue;
    }

    protected int getKeepLastNValues()
    {
        return this.keepLastNValues;
    }
}
