package de.freese.jconky.sensor.cpu;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public record CpuCore(int core, int frequency, double temperature, double usage) {
}
