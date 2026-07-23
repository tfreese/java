// Created: 16 März 2025
package de.freese.sonstiges.xml.jaxb.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * @author Thomas Freese
 */
public class OpeningDateJsonAdapter implements JsonbAdapter<LocalDate, String> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate adaptFromJson(final String localDate) {
        return LocalDate.parse(localDate, DATE_TIME_FORMATTER);
    }

    @Override
    public String adaptToJson(final LocalDate localDate) {
        return DATE_TIME_FORMATTER.format(localDate);
    }
}
