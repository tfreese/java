package de.freese.jconky;

import java.util.HashMap;
import java.util.Map;

import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.system.SystemMonitor;

/**
 * @author Thomas Freese
 * @since 13.12.2020
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

    private final ProcessInfos processInfos = new ProcessInfos();
    private final Map<String, TemperatureInfo> temperatures = new HashMap<>();
    private final Map<String, UsageInfo> usages = new HashMap<>();
    private MusicInfo musicInfo = new MusicInfo();
    private long totalSystemMemory;
    private int updates;
    private double uptimeInSeconds;

    private Context() {
        super();
    }

    public MusicInfo getMusicInfo() {
        return musicInfo;
    }

    public ProcessInfos getProcessInfos() {
        return processInfos;
    }

    public Map<String, TemperatureInfo> getTemperatures() {
        return temperatures;
    }

    public long getTotalSystemMemory() {
        return totalSystemMemory;
    }

    public int getUpdates() {
        return updates;
    }

    public double getUptimeInSeconds() {
        return uptimeInSeconds;
    }

    public Map<String, UsageInfo> getUsages() {
        return usages;
    }

    public void updateMusicInfo() {
        try {
            musicInfo = getSystemMonitor().getMusicInfo();
        }
        catch (final Exception ex) {
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
