package de.freese.sonstiges.julianday;

import java.time.LocalDate;

/**
 * Das julianische Datum (JD) ist eine fortlaufende Zählung von Tagen seit dem 1. Januar 4713 v. Chr. (12:00 Uhr Weltzeit).
 *
 * @author Thomas Freese
 */
public final class JulianDayConverter {
    /**
     * Berechnet den Wert des Julianischen Tages.
     */
    public static int calculateJD(final int year, final int month, final int day) {
        final int y = ((240 * year) + (20 * month)) - 57;
        final int a = (((((367 * y) / 240) * 4) - (7 * (y / 240))) + (4 * day)) / 4;
        final int b = ((4 * a) - (3 * (y / 24_000))) / 4;

        return b + 1_721_115;
    }

    /**
     * Liefert einen lesbaren Wert des Jahres, Monats und Tag.
     *
     * @return int; 19700101
     */
    public static int calculateReadable(final int year, final int month, final int day) {
        return (year * 10_000) + (month * 100) + day;
    }

    /**
     * Berechnet das Tagesobjekt aus dem Julianischen Wert.
     */
    public static LocalDate createLocalDateFromJD(final int julianDay) {
        final int g = ((julianDay << 2) - 7_468_865) / 146_097;
        final int a = (julianDay + 1 + g) - (g >> 2);
        final int b = a + 1524;
        final int c = ((20 * b) - 2_442) / 7_305;
        final int d = (1461 * c) >> 2;
        final int e = (10_000 * (b - d)) / 306_001;
        final int day = b - d - ((306_001 * e) / 10_000);
        final int month = e < 14 ? e - 1 : e - 13;
        final int year = month > 2 ? c - 4_716 : c - 4_715;

        return LocalDate.of(year, month, day);
    }

    /**
     * Berechnet das Tagesobjekt aus dem lesbaren Wert.
     *
     * @param readableDay int; 19700101
     *
     * @return {@link LocalDate}; 01.01.1970
     */
    public static LocalDate createLocalDateFromReadable(final int readableDay) {
        final int jd = calculateJD(readableDay / 10_000, (readableDay % 10_000) / 100, readableDay % 100);

        return createLocalDateFromJD(jd);
    }

    private JulianDayConverter() {
        super();
    }
}
