package de.freese.jconky.sensor.cpu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public final class Cpu {
    private final List<CpuCore> cpuCores;
    private final CpuLoadAvg cpuLoadAvg;
    private final double temperature;
    private final double usage;

    public Cpu(final double temperature, final double usage, final CpuLoadAvg cpuLoadAvg) {
        super();

        this.temperature = temperature;
        this.usage = usage;
        this.cpuLoadAvg = Objects.requireNonNull(cpuLoadAvg, "cpuLoadAvg required");

        cpuCores = new ArrayList<>();
    }

    public void addCore(final CpuCore cpuCore) {
        cpuCores.add(cpuCore);
    }

    public CpuCore getCore(final int core) {
        return cpuCores.get(core);
    }

    public CpuLoadAvg getCpuLoadAvg() {
        return cpuLoadAvg;
    }

    public int getNumberOfCores() {
        return cpuCores.size();
    }

    public double getTemperature() {
        return temperature;
    }

    public double getUsage() {
        return usage;
    }
}
