// Created: 05 Juni 2025
package de.freese.jconky.system;

import java.util.Map;

import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.model.UsageInfo;

/**
 * @author Thomas Freese
 */
public final class WindowsSystemMonitor extends AbstractSystemMonitor {
    private final ProcessBuilder processBuilderCpu;
    private final ProcessBuilder processBuilderFree;
    private final ProcessBuilder processBuilderTop;
    private final ProcessBuilder processBuilderUser;

    public WindowsSystemMonitor() {
        super();

        final boolean is32bit = System.getProperty("sun.arch.data.model").contains("32");
        final String systemPath = is32bit ? "c:\\windows\\Sysnative" : "c:\\windows\\system32";

        this.processBuilderCpu = new ProcessBuilder(systemPath + "\\wbem\\wmic.exe", "cpu get loadpercentage");
        this.processBuilderFree = new ProcessBuilder(systemPath + "\\wbem\\wmic.exe", "memorychip get capacity");
        this.processBuilderTop = new ProcessBuilder(systemPath + "\\tasklist.exe", "/Nh");
        this.processBuilderUser = new ProcessBuilder(systemPath + "\\change.exe", "logon /query");
    }

    @Override
    public CpuInfos getCpuInfos() {
        return null;
    }

    @Override
    public CpuLoadAvg getCpuLoadAvg() {
        return null;
    }

    @Override
    public String getExternalIp() {
        return "";
    }

    @Override
    public Map<String, UsageInfo> getFilesystems() {
        return Map.of();
    }

    @Override
    public HostInfo getHostInfo() {
        return null;
    }

    @Override
    public MusicInfo getMusicInfo() {
        return null;
    }

    @Override
    public NetworkInfos getNetworkInfos() {
        return null;
    }

    @Override
    public ProcessInfos getProcessInfos(final double uptimeInSeconds, final long totalSystemMemory) {
        return null;
    }

    @Override
    public Map<String, UsageInfo> getRamAndSwap() {
        return Map.of();
    }

    @Override
    public Map<String, TemperatureInfo> getTemperatures() {
        return Map.of();
    }

    @Override
    public int getUpdates() {
        return 0;
    }

    @Override
    public double getUptimeInSeconds() {
        return 0;
    }
}
