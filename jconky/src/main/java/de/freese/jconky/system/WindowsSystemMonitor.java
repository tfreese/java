package de.freese.jconky.system;

import java.util.Map;

import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.model.UsageInfo;

/**
 * @author Thomas Freese
 * @since 05.06.2025
 */
public final class WindowsSystemMonitor extends AbstractSystemMonitor {
    // private final ProcessBuilder processBuilderCpu;
    // private final ProcessBuilder processBuilderFree;
    // private final ProcessBuilder processBuilderTop;
    // private final ProcessBuilder processBuilderUser;

    public WindowsSystemMonitor() {
        super();

        // final boolean is32bit = System.getProperty("sun.arch.data.model").contains("32");
        // final String systemPath = is32bit ? "c:\\windows\\Sysnative" : "c:\\windows\\system32";

        // processBuilderCpu = new ProcessBuilder(systemPath + "\\wbem\\wmic.exe", "cpu get loadpercentage");
        // processBuilderFree = new ProcessBuilder(systemPath + "\\wbem\\wmic.exe", "memorychip get capacity");
        // processBuilderTop = new ProcessBuilder(systemPath + "\\tasklist.exe", "/Nh");
        // processBuilderUser = new ProcessBuilder(systemPath + "\\change.exe", "logon /query");
    }

    @Override
    public Map<String, UsageInfo> getFilesystems() {
        return Map.of();
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
