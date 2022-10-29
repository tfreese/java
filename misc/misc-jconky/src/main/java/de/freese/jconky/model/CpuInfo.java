// Created: 05.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class CpuInfo
{
    /**
     *
     */
    private final int core;
    /**
     *
     */
    private final CpuTimes cpuTimes;
    /**
     *
     */
    private final int frequency;
    /**
     *
     */
    private final double temperature;
    /**
     *
     */
    private double usage;

    /**
     * Erstellt ein neues {@link CpuInfo} Object.
     */
    public CpuInfo()
    {
        this(-1, 0D, 0, new CpuTimes());
    }

    /**
     * Erstellt ein neues {@link CpuInfo} Object.
     *
     * @param core int
     * @param temperature double
     * @param frequency int
     * @param cpuTimes {@link CpuTimes}
     */
    public CpuInfo(final int core, final double temperature, final int frequency, final CpuTimes cpuTimes)
    {
        super();

        this.core = core;
        this.temperature = temperature;
        this.frequency = frequency;
        this.cpuTimes = cpuTimes;
    }

    /**
     * Berechnet die CPU-Auslastung von 0 bis 1.<br>
     *
     * @param previous {@link CpuInfo}
     */
    public void calculateCpuUsage(final CpuInfo previous)
    {
        this.usage = getCpuTimes().getCpuUsage(previous.getCpuTimes());
    }

    /**
     * @return int
     */
    public int getCore()
    {
        return this.core;
    }

    /**
     * @return {@link CpuTimes}
     */
    public CpuTimes getCpuTimes()
    {
        return this.cpuTimes;
    }

    /**
     * Liefert die CPU-Auslastung von 0 bis 1.<br>
     *
     * @return double
     */
    public double getCpuUsage()
    {
        return this.usage;
    }

    /**
     * @return int
     */
    public int getFrequency()
    {
        return this.frequency;
    }

    /**
     * @return double
     */
    public double getTemperature()
    {
        return this.temperature;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("core=").append(this.core);
        builder.append(", usage=").append(this.usage);
        builder.append(", temperature=").append(this.temperature);
        builder.append(", frequency=").append(this.frequency);
        builder.append("]");

        return builder.toString();
    }
}
