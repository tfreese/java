// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class TemperatureInfo {
    private final String device;
    private final double temperature;

    public TemperatureInfo() {
        this("", 0D);
    }

    public TemperatureInfo(final String device, final double temperature) {
        super();

        this.device = device;
        this.temperature = temperature;
    }

    public String getDevice() {
        return device;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + " device=" + device
                + ", temperature=" + temperature
                + "]";
    }
}
