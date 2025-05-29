package de.freese.sonstiges.julianday;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * TestKlasse.
 *
 * @author Thomas Freese
 */
class TestJulianDay {
    @Test
    void testBerechnungMonat() {
        final LocalDate ld1 = LocalDate.of(2012, 7, 8); // 08.07.2012
        final int jd = JulianDayConverter.calculateJD(ld1);
        assertEquals(2456117, jd);

        final LocalDate ld2 = JulianDayConverter.createLocalDateFromReadable(jd);
        assertEquals(LocalDate.of(2012, 7, 8), ld2);
        assertEquals("2012-07-08", ld2.toString());
    }

    @Test
    void testBerechnungTag() {
        final Day d1 = new Day(2012, 7, -2); // 28.06.2012
        final int jd = JulianDayConverter.calculateJD(d1);
        assertEquals(2456107, jd);

        final Day d2 = JulianDayConverter.createDayFromJD(jd);
        assertEquals(new Day(2012, 6, 28), d2);
        assertEquals("2012-06-28", d2.toString());
    }

    @Test
    void testKonvertierungJavaUtilDate() {
        final Date date = new Date(1344925513600L); // Tue Aug 14 08:25:13 CEST 2012
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.setTime(date);

        final int jdBerlin = JulianDayConverter.calculateJD(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("2012-08-14", JulianDayConverter.createDayFromJD(jdBerlin).toString());

        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        final int jdLosAngeles = JulianDayConverter.calculateJD(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("2012-08-13", JulianDayConverter.createDayFromJD(jdLosAngeles).toString());
    }

    @Test
    void testReadable() {
        final Day d1 = new Day(2011, 19, 8); // 08.07.2012
        final int readable1 = JulianDayConverter.calculateReadable(d1);
        final int jd = JulianDayConverter.calculateJD(d1);
        assertEquals(20111908, readable1);

        final Day d2 = JulianDayConverter.createDayFromReadable(readable1);
        assertEquals(d1, d2);
        assertEquals(d1.toString(), d2.toString());
        assertEquals("2011-19-08", d2.toString());

        final Day d3 = JulianDayConverter.createDayFromJD(jd);
        assertEquals("2012-07-08", d3.toString());
    }

    @Test
    void testVergleich() {
        final Day d1 = new Day(1990, 1, 1);
        final int jd = JulianDayConverter.calculateJD(d1);
        assertEquals(2447893, jd);

        final Day d2 = JulianDayConverter.createDayFromJD(jd);
        assertEquals(d1, d2);
        assertEquals(d1.toString(), d2.toString());
        assertEquals("1990-01-01", d2.toString());
    }

    @Test
    void testWochentagsErmittlung() {
        final Day d1 = new Day(2012, 8, 6);
        final int jd = JulianDayConverter.calculateJD(d1);
        assertEquals(0, jd % 7); // Montag == 0

        final Day d2 = JulianDayConverter.createDayFromJD(jd);
        assertEquals(d1, d2);
        assertEquals(d1.toString(), d2.toString());
        assertEquals("2012-08-06", d2.toString());
    }
}
