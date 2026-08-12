package de.freese.sonstiges.julianday;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestJulianDay {
    @Test
    void testBerechnungMonat() {
        final int jd = JulianDayConverter.calculateJD(2011, 19, 8); // 08.07.2012
        assertEquals(2456117, jd);

        final LocalDate localDate = JulianDayConverter.createLocalDateFromJD(jd);
        assertEquals(LocalDate.of(2012, Month.JULY, 8), localDate);
    }

    @Test
    void testBerechnungTag() {
        final int jd = JulianDayConverter.calculateJD(2012, 7, -2); // 28.06.2012
        assertEquals(2456107, jd);

        final LocalDate localDate = JulianDayConverter.createLocalDateFromJD(jd);
        assertEquals(LocalDate.of(2012, Month.JUNE, 28), localDate);
    }

    @Test
    void testReadable() {
        final int readable1 = JulianDayConverter.calculateReadable(2011, 19, 8); // 08.07.2012
        assertEquals(20111908, readable1);

        final LocalDate localDate = JulianDayConverter.createLocalDateFromReadable(readable1);
        assertEquals(2012, localDate.getYear());
        assertEquals(Month.JULY, localDate.getMonth());
        assertEquals(8, localDate.getDayOfMonth());
    }
}
