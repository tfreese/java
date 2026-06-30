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
public interface SystemMonitor {
    CpuInfos getCpuInfos();

    CpuLoadAvg getCpuLoadAvg();

    String getExternalIp();

    Map<String, UsageInfo> getFilesystems();

    HostInfo getHostInfo();

    MusicInfo getMusicInfo();

    NetworkInfos getNetworkInfos();

    int getNumberOfCores();

    ProcessInfos getProcessInfos(double uptimeInSeconds, long totalSystemMemory);

    Map<String, UsageInfo> getRamAndSwap();

    Map<String, TemperatureInfo> getTemperatures();

    long getTotalSystemMemory();

    int getUpdates();

    double getUptimeInSeconds();
}
