// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Thomas Freese
 */
public interface SensorValue
{
    /**
     * @return {@link Date}
     */
    default Date getDate()
    {
        return new Date(getTimestamp());
    }

    /**
     * @return {@link LocalDateTime}
     */
    default LocalDateTime getLocalDateTime()
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(getTimestamp()), TimeZone.getDefault().toZoneId());
    }

    /**
     * Name of the Sensor.
     *
     * @return String
     */
    String getName();

    /**
     * @return long
     */
    long getTimestamp();

    /**
     * @return String
     */
    String getValue();

    /**
     * @return double
     */
    double getValueAsDouble();

    /**
     * @return long
     */
    long getValueAsLong();
}
