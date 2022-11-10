// Created: 05.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class CpuLoadAvg
{
    private final double fifteenMinutes;

    private final double fiveMinutes;

    private final double oneMinute;

    public CpuLoadAvg()
    {
        this(0D, 0D, 0D);

    }

    public CpuLoadAvg(final double oneMinute, final double fiveMinutes, final double fifteenMinutes)
    {
        super();

        this.oneMinute = oneMinute;
        this.fiveMinutes = fiveMinutes;
        this.fifteenMinutes = fifteenMinutes;
    }

    public double getFifteenMinutes()
    {
        return this.fifteenMinutes;
    }

    public double getFiveMinutes()
    {
        return this.fiveMinutes;
    }

    public double getOneMinute()
    {
        return this.oneMinute;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("oneMinute=").append(this.oneMinute);
        builder.append(", fiveMinutes=").append(this.fiveMinutes);
        builder.append(", fifteenMinutes=").append(this.fifteenMinutes);
        builder.append("]");

        return builder.toString();
    }
}
