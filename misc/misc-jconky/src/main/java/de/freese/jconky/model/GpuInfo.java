// Created: 23.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class GpuInfo extends TemperatureInfo
{
    /**
     *
     */
    private final int fanSpeed;
    /**
     *
     */
    private final double power;
    /**
     *
     */
    private final int usage;

    /**
     * Erstellt ein neues {@link GpuInfo} Object.
     */
    public GpuInfo()
    {
        this(0D, 0D, 0, 0);
    }

    /**
     * Erstellt ein neues {@link GpuInfo} Object.
     *
     * @param temperature double
     * @param power double
     * @param fanSpeed int
     * @param usage int
     */
    public GpuInfo(final double temperature, final double power, final int fanSpeed, final int usage)
    {
        super("GPU", temperature);

        this.power = power;
        this.fanSpeed = fanSpeed;
        this.usage = usage;
    }

    /**
     * @return int
     */
    public int getFanSpeed()
    {
        return this.fanSpeed;
    }

    /**
     * @return double
     */
    public double getPower()
    {
        return this.power;
    }

    /**
     * @return int
     */
    public int getUsage()
    {
        return this.usage;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" device=").append(getDevice());
        builder.append(", temperature=").append(getTemperature());
        builder.append(", power=").append(this.power);
        builder.append(", fanSpeed=").append(this.fanSpeed);
        builder.append(", usage=").append(this.usage);
        builder.append("]");

        return builder.toString();
    }
}
