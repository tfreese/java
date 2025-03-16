// Created: 16 März 2025
package de.freese.sonstiges.xml.jaxb.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * @author Thomas Freese
 */
public class OpeningDateJsonAdapter implements JsonbAdapter<Date, String> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Date adaptFromJson(final String date) {
        final LocalDate localDate = LocalDate.parse(date, DATE_TIME_FORMATTER);
        final Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
    }

    @Override
    public String adaptToJson(final Date date) {
        final Instant instant = date.toInstant();
        final LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        return DATE_TIME_FORMATTER.format(localDate);
    }
}
