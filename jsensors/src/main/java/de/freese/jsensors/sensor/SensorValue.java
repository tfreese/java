// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public interface SensorValue {
    default Date getDate() {
        return new Date(timestamp());
    }

    default LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp()), ZoneId.systemDefault());
    }

    default <T> T getValueAs(final Function<String, T> function) {
        return function.apply(value());
    }

    default double getValueAsDouble() {
        return getValueAs(Double::parseDouble);
    }

    default long getValueAsLong() {
        return getValueAs(Long::parseLong);
    }

    String name();

    long timestamp();

    String value();
}
