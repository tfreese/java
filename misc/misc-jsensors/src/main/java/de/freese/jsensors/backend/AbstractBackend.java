// Created: 31.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Implementation for a {@link Backend}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBackend implements Backend
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see de.freese.jsensors.backend.Backend#store(de.freese.jsensors.sensor.SensorValue)
     */
    @Override
    public final void store(final SensorValue sensorValue)
    {
        if (sensorValue == null)
        {
            getLogger().warn("sensorValue is null");
            return;
        }

        if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
        {
            getLogger().warn("sensorValue without content");
            return;
        }

        getLogger().debug("{}", sensorValue);

        try
        {
            storeValue(sensorValue);
        }
        catch (Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    protected abstract void storeValue(SensorValue sensorValue);
}
