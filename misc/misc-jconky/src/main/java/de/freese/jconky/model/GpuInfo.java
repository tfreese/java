// Created: 23.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class GpuInfo extends TemperatureInfo {
    private final int fanSpeed;
    private final double power;
    private final int usage;

    public GpuInfo() {
        this(0D, 0D, 0, 0);
    }

    public GpuInfo(final double temperature, final double power, final int fanSpeed, final int usage) {
        super("GPU", temperature);

        this.power = power;
        this.fanSpeed = fanSpeed;
        this.usage = usage;
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

    public double getPower() {
        return power;
    }

    public int getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" device=").append(getDevice());
        builder.append(", temperature=").append(getTemperature());
        builder.append(", power=").append(power);
        builder.append(", fanSpeed=").append(fanSpeed);
        builder.append(", usage=").append(usage);
        builder.append("]");

        return builder.toString();
    }
}
