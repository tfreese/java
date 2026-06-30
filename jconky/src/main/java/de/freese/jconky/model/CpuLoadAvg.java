// Created: 05.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public record CpuLoadAvg(double oneMinute, double fiveMinutes, double fifteenMinutes) {
    public CpuLoadAvg() {
        this(0D, 0D, 0D);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + "oneMinute=" + oneMinute
                + ", fiveMinutes=" + fiveMinutes
                + ", fifteenMinutes=" + fifteenMinutes
                + "]";
    }
}
