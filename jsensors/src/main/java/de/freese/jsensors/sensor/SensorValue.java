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
    default Date getDate()
    {
        return new Date(getTimestamp());
    }

    default LocalDateTime getLocalDateTime()
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(getTimestamp()), TimeZone.getDefault().toZoneId());
    }

    String getName();

    long getTimestamp();

    String getValue();

    double getValueAsDouble();

    long getValueAsLong();
}
