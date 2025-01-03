package de.freese.sonstiges.julianday;

import java.io.Serial;
import java.io.Serializable;

/**
 * Enthält die Daten des Tages (Year, Month, Day).
 *
 * @author Thomas Freese
 */
public class Day implements Serializable {
    @Serial
    private static final long serialVersionUID = 4560714541840783730L;

    private final int dayOfMonth;
    private final int month;
    private final int year;

    public Day(final int year, final int month, final int day) {
        super();

        this.year = year;
        this.month = month;
        this.dayOfMonth = day;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Day other)) {
            return false;
        }

        if (dayOfMonth != other.dayOfMonth) {
            return false;
        }

        if (month != other.month) {
            return false;
        }

        return year == other.year;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public int hashCode() {
        // final int prime = 31;
        // int result = 1;
        // result = prime * result + this.day;
        // result = prime * result + this.month;
        // result = prime * result + this.year;
        //
        // return result;

        return (year * 400) + (month * 31) + dayOfMonth;
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d", year, month, dayOfMonth);
    }
}
