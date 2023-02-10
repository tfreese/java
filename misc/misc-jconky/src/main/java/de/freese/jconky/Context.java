// Created: 13.12.2020
package de.freese.jconky;

import java.util.HashMap;
import java.util.Map;

import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.system.SystemMonitor;

/**
 * @author Thomas Freese
 */
public final class Context {
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class JConkyContextHolder {
        private static final Context INSTANCE = new Context();

        private JConkyContextHolder() {
            super();
        }
    }

    public static Context getInstance() {
        return JConkyContextHolder.INSTANCE;
    }

    private CpuInfos cpuInfos = new CpuInfos();

    private CpuLoadAvg cpuLoadAvg = new CpuLoadAvg();

    private String externalIp = "";

    private HostInfo hostInfo = new HostInfo();

    private MusicInfo musicInfo = new MusicInfo();

    private NetworkInfos networkInfos = new NetworkInfos();

    private int numberOfCores;

    private ProcessInfos processInfos = new ProcessInfos();

    private Map<String, TemperatureInfo> temperatures = new HashMap<>();

    private long totalSystemMemory;

    private int updates;

    private double uptimeInSeconds;

    private Map<String, UsageInfo> usages = new HashMap<>();

    private Context() {
        super();
    }

    public CpuInfos getCpuInfos() {
        return this.cpuInfos;
    }

    public CpuLoadAvg getCpuLoadAvg() {
        return this.cpuLoadAvg;
    }

    public String getExternalIp() {
        return this.externalIp;
    }

    public HostInfo getHostInfo() {
        return this.hostInfo;
    }

    public MusicInfo getMusicInfo() {
        return this.musicInfo;
    }

    public NetworkInfos getNetworkInfos() {
        return this.networkInfos;
    }

    public int getNumberOfCores() {
        return this.numberOfCores;
    }

    public ProcessInfos getProcessInfos() {
        return this.processInfos;
    }

    public Map<String, TemperatureInfo> getTemperatures() {
        return this.temperatures;
    }

    public long getTotalSystemMemory() {
        return this.totalSystemMemory;
    }

    public int getUpdates() {
        return this.updates;
    }

    public double getUptimeInSeconds() {
        return this.uptimeInSeconds;
    }

    public Map<String, UsageInfo> getUsages() {
        return this.usages;
    }

    public void updateCpuInfos() {
        try {
            this.cpuLoadAvg = getSystemMonitor().getCpuLoadAvg();

            // CpuUsages berechnen.
            CpuInfos cpuInfosPrevious = this.cpuInfos;
            this.cpuInfos = getSystemMonitor().getCpuInfos();

            this.cpuInfos.getTotal().calculateCpuUsage(cpuInfosPrevious.getTotal());

            for (int i = 0; i < getNumberOfCores(); i++) {
                this.cpuInfos.get(i).calculateCpuUsage(cpuInfosPrevious.get(i));
            }
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateHostInfo() {
        try {
            this.hostInfo = getSystemMonitor().getHostInfo();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateMusicInfo() {
        try {
            this.musicInfo = getSystemMonitor().getMusicInfo();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * Netzwerk: Download/Upload berechnen.
     */
    public void updateNetworkInfos() {
        try {
            NetworkInfos networkInfosPrevious = this.networkInfos;
            this.networkInfos = getSystemMonitor().getNetworkInfos();

            this.networkInfos.calculateUpAndDownload(networkInfosPrevious);
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * Daten, die einmal ermittelt werden müssen.
     */
    public void updateOneShot() {
        try {
            this.numberOfCores = getSystemMonitor().getNumberOfCores();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }

        try {
            this.totalSystemMemory = getSystemMonitor().getTotalSystemMemory();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }

        try {
            this.externalIp = getSystemMonitor().getExternalIp();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateProcessInfos() {
        try {
            this.processInfos = getSystemMonitor().getProcessInfos(getUptimeInSeconds(), getTotalSystemMemory());
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateTemperatures() {
        try {
            this.temperatures = getSystemMonitor().getTemperatures();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateUpdates() {
        try {
            this.updates = getSystemMonitor().getUpdates();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void updateUptimeInSeconds() {
        try {
            this.uptimeInSeconds = getSystemMonitor().getUptimeInSeconds();
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * Daten, die alle paar Sekunden aktualisiert werden müssen.
     */
    public void updateUsages() {
        try {
            Map<String, UsageInfo> map = new HashMap<>();
            map.putAll(getSystemMonitor().getRamAndSwap());
            map.putAll(getSystemMonitor().getFilesystems());

            this.usages = map;
        }
        catch (Exception ex) {
            JConky.getLogger().error(ex.getMessage(), ex);
        }
    }

    private Settings getSettings() {
        return Settings.getInstance();
    }

    private SystemMonitor getSystemMonitor() {
        return getSettings().getSystemMonitor();
    }
}
