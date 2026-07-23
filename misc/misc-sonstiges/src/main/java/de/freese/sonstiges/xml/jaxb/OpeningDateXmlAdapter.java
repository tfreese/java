// Created: 21.01.2010
package de.freese.sonstiges.xml.jaxb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class OpeningDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // /**
    // * Is not Thread-Safe!
    // */
    // private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(final LocalDate localDate) throws Exception {
        return DATE_TIME_FORMATTER.format(localDate);
    }

    @Override
    public LocalDate unmarshal(final String localDate) throws Exception {
        return LocalDate.parse(localDate, DATE_TIME_FORMATTER);
    }
}
