package de.freese.jconky.sensor.cpu;

/**
 * @author Thomas Freese
 * @since 27.09.26
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
