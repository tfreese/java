package de.freese.jconky.system;

import java.util.Map;

import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;

/**
 * @author Thomas Freese
 * @since 01.12.2020
 */
public interface SystemMonitor {
    String getExternalIp();

    MusicInfo getMusicInfo();

    NetworkInfos getNetworkInfos();

    ProcessInfos getProcessInfos(double uptimeInSeconds, long totalSystemMemory);

    Map<String, TemperatureInfo> getTemperatures();

    long getTotalSystemMemory();

    int getUpdates();

    double getUptimeInSeconds();
}
