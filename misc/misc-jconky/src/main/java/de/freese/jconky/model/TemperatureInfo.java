// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class TemperatureInfo
{
    private final String device;

    private final double temperature;

    public TemperatureInfo()
    {
        this("", 0D);
    }

    public TemperatureInfo(final String device, final double temperature)
    {
        super();

        this.device = device;
        this.temperature = temperature;
    }

    public String getDevice()
    {
        return this.device;
    }

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
        builder.append(" device=").append(this.device);
        builder.append(", temperature=").append(this.temperature);
        builder.append("]");

        return builder.toString();
    }
}
