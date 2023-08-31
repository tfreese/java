// Created: 21.01.2010
package de.freese.sonstiges.xml.jaxb;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class OpeningDateAdapter extends XmlAdapter<String, Date> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // /**
    // * Ist nicht Thread-Safe !
    // */
    // private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(final Date date) throws Exception {
        Instant instant = date.toInstant();
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        return DATE_TIME_FORMATTER.format(localDate);
        // return FORMATTER.format(date);
    }

    @Override
    public Date unmarshal(final String date) throws Exception {
        LocalDate localDate = LocalDate.parse(date, DATE_TIME_FORMATTER);
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
        // return FORMATTER.parse(date);
    }
}
