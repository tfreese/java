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

    private transient String asString;

    public Day(final int year, final int month, final int day) {
        super();

        this.year = year;
        this.month = month;
        this.dayOfMonth = day;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Day other)) {
            return false;
        }

        if (this.dayOfMonth != other.dayOfMonth) {
            return false;
        }

        if (this.month != other.month) {
            return false;
        }

        return this.year == other.year;
    }

    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // final int prime = 31;
        // int result = 1;
        // result = prime * result + this.day;
        // result = prime * result + this.month;
        // result = prime * result + this.year;
        //
        // return result;

        return (this.year * 400) + (this.month * 31) + this.dayOfMonth;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (this.asString == null) {
            this.asString = String.format("%d-%02d-%02d", this.year, this.month, this.dayOfMonth);
        }

        return this.asString;
    }
}
