// Created: 05.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class CpuLoadAvg {
    private final double fifteenMinutes;
    private final double fiveMinutes;
    private final double oneMinute;

    public CpuLoadAvg() {
        this(0D, 0D, 0D);

    }

    public CpuLoadAvg(final double oneMinute, final double fiveMinutes, final double fifteenMinutes) {
        super();

        this.oneMinute = oneMinute;
        this.fiveMinutes = fiveMinutes;
        this.fifteenMinutes = fifteenMinutes;
    }

    public double getFifteenMinutes() {
        return fifteenMinutes;
    }

    public double getFiveMinutes() {
        return fiveMinutes;
    }

    public double getOneMinute() {
        return oneMinute;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("oneMinute=").append(oneMinute);
        builder.append(", fiveMinutes=").append(fiveMinutes);
        builder.append(", fifteenMinutes=").append(fifteenMinutes);
        builder.append("]");

        return builder.toString();
    }
}
