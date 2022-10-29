// Created: 01.12.2020
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
public interface SystemMonitor
{
    /**
     * @return {@link CpuInfos}
     */
    CpuInfos getCpuInfos();

    /**
     * @return {@link CpuLoadAvg}
     */
    CpuLoadAvg getCpuLoadAvg();

    /**
     * @return String
     */
    String getExternalIp();

    /**
     * @return {@link Map}
     */
    Map<String, UsageInfo> getFilesystems();

    /**
     * @return {@link HostInfo}
     */
    HostInfo getHostInfo();

    /**
     * @return {@link MusicInfo}
     */
    MusicInfo getMusicInfo();

    /**
     * @return {@link NetworkInfos}
     */
    NetworkInfos getNetworkInfos();

    /**
     * @return int
     */
    int getNumberOfCores();

    /**
     * @param uptimeInSeconds double
     * @param totalSystemMemory long
     *
     * @return {@link ProcessInfos}
     */
    ProcessInfos getProcessInfos(double uptimeInSeconds, long totalSystemMemory);

    /**
     * @return {@link Map}
     */
    Map<String, UsageInfo> getRamAndSwap();

    /**
     * @return {@link Map}
     */
    Map<String, TemperatureInfo> getTemperatures();

    /**
     * @return long
     */
    long getTotalSystemMemory();

    /**
     * @return int
     */
    int getUpdates();

    /**
     * @return double
     */
    double getUptimeInSeconds();
}
