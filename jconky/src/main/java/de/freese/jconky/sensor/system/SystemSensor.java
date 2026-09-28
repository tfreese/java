package de.freese.jconky.sensor.system;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.sensor.AbstractSensor;

/**
 * @author Thomas Freese
 * @since 28.09.26
 */
public final class SystemSensor extends AbstractSensor {
    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private Map<String, UsageInfo> usages = new HashMap<>();

    public Map<String, UsageInfo> getUsages() {
        return usages;
    }

    @Override
    public void update() {
        final Map<String, UsageInfo> map = new HashMap<>();
        map.putAll(getRamAndSwap());
        map.putAll(getFilesystems());

        usages = map;

        // new ProcessBuilder("/bin/sh", "-c", "checkupdates");
    }

    private Map<String, UsageInfo> getFilesystems() {
        final Map<String, UsageInfo> map = new HashMap<>();

        final FileSystem defaultFileSystem = FileSystems.getDefault();

        for (final FileStore store : defaultFileSystem.getFileStores()) {
            try {
                final String path = store.toString();

                final long total = store.getTotalSpace();
                final long used = total - store.getUnallocatedSpace();
                final long free = store.getUsableSpace();

                if (path.startsWith("/ ") || path.startsWith("/tmp ")) {
                    final String[] splits = path.split(SPACE_PATTERN.pattern(), -1);
                    map.put(splits[0], new UsageInfo(splits[0], total, used, free));
                }
            }
            catch (final IOException ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }

        return map;
    }

    /**
     * /proc/meminfo
     */
    private Map<String, UsageInfo> getRamAndSwap() {
        final Map<String, UsageInfo> map = new HashMap<>();

        final long memoryTotal = OPERATING_SYSTEM_MX_BEAN.getTotalMemorySize();
        final long memoryFree = OPERATING_SYSTEM_MX_BEAN.getFreeMemorySize();

        final UsageInfo ramUsageInfo = new UsageInfo(
                "RAM1",
                memoryTotal,
                memoryTotal - memoryFree,
                memoryFree);
        map.put(ramUsageInfo.path(), ramUsageInfo);

        final long swapTotal = OPERATING_SYSTEM_MX_BEAN.getTotalSwapSpaceSize();
        final long swapFree = OPERATING_SYSTEM_MX_BEAN.getFreeSwapSpaceSize();

        final UsageInfo swapUsageInfo = new UsageInfo(
                "SWAP1",
                swapTotal,
                swapTotal - swapFree,
                swapFree);
        map.put(swapUsageInfo.path(), swapUsageInfo);

        final ProcessBuilder processBuilderSensors = new ProcessBuilder("/bin/sh", "-c", "free --bytes");
        final List<String> lines = readContent(processBuilderSensors);

        for (int i = 0; i < lines.size(); i++) {
            if (i == 1) {
                // Speicher
                final String line = lines.get(i).replace(":", ": ");
                final String[] splits = SPACE_PATTERN.split(line, -1);
                final long size = Long.parseLong(splits[1]);
                final long used = Long.parseLong(splits[2]);
                final long free = Long.parseLong(splits[3]);

                map.put("RAM", new UsageInfo("RAM", size, used, free));
            }
            else if (i == 2) {
                // Swap
                final String line = lines.get(i).replace(":", ": ");
                final String[] splits = SPACE_PATTERN.split(line);
                final long size = Long.parseLong(splits[1]);
                final long used = Long.parseLong(splits[2]);
                final long free = Long.parseLong(splits[3]);

                map.put("SWAP", new UsageInfo("SWAP", size, used, free));
            }
        }

        return map;
    }
}
