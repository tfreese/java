// Created: 05.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class CpuInfo {
    private final int core;
    private final CpuTimes cpuTimes;
    private final int frequency;
    private final double temperature;
    private double usage;

    public CpuInfo() {
        this(-1, 0D, 0, new CpuTimes());
    }

    public CpuInfo(final int core, final double temperature, final int frequency, final CpuTimes cpuTimes) {
        super();

        this.core = core;
        this.temperature = temperature;
        this.frequency = frequency;
        this.cpuTimes = cpuTimes;
    }

    /**
     * Berechnet die CPU-Auslastung von 0 bis 1.<br>
     */
    public void calculateCpuUsage(final CpuInfo previous) {
        usage = getCpuTimes().getCpuUsage(previous.getCpuTimes());
    }

    public int getCore() {
        return core;
    }

    public CpuTimes getCpuTimes() {
        return cpuTimes;
    }

    /**
     * Liefert die CPU-Auslastung von 0 bis 1.<br>
     */
    public double getCpuUsage() {
        return usage;
    }

    public int getFrequency() {
        return frequency;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("core=").append(core);
        builder.append(", usage=").append(usage);
        builder.append(", temperature=").append(temperature);
        builder.append(", frequency=").append(frequency);
        builder.append("]");

        return builder.toString();
    }
}
