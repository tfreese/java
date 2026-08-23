package de.freese.jconky.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.UsageInfo;

/**
 * @author Thomas Freese
 * @since 01.12.2020
 */
public abstract class AbstractSystemMonitor implements SystemMonitor {
    /**
     * "[ ]" = "\\s+" = Whitespace: einer oder mehrere
     * Tabs, NewLines are supported.
     * Tab only: "\t+"
     */
    protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final long myPid;

    protected AbstractSystemMonitor() {
        super();

        myPid = ProcessHandle.current().pid();
    }

    @Override
    public CpuInfos getCpuInfos() {
        return new CpuInfos();
    }

    @Override
    public CpuLoadAvg getCpuLoadAvg() {
        return new CpuLoadAvg(
                OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage() * 10D,
                OPERATING_SYSTEM_MX_BEAN.getCpuLoad() * 10D,
                OPERATING_SYSTEM_MX_BEAN.getProcessCpuLoad() * 10D);
    }

    @Override
    public String getExternalIp() {
        String externalIp = "";

        try {
            // final URL url = URI.create("https://ifconfig.me/ip").toURL();
            final URL url = URI.create("https://4.ident.me").toURL();
            final URLConnection connection = url.openConnection();
            // connection.connect();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                externalIp = br.readLine();
            }
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return externalIp;
    }

    @Override
    public Map<String, UsageInfo> getFilesystems() {
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

    @Override
    public HostInfo getHostInfo() {
        return new HostInfo(
                OPERATING_SYSTEM_MX_BEAN.getName(),
                OPERATING_SYSTEM_MX_BEAN.getVersion(),
                OPERATING_SYSTEM_MX_BEAN.getArch()
        );
    }

    /**
     * Liefert die eigene Process-ID
     */
    public long getMyPid() {
        return myPid;
    }

    @Override
    public int getNumberOfCores() {
        // return Runtime.getRuntime().availableProcessors();
        return OPERATING_SYSTEM_MX_BEAN.getAvailableProcessors();
    }

    @Override
    public Map<String, UsageInfo> getRamAndSwap() {
        final Map<String, UsageInfo> map = new HashMap<>();

        final long memoryTotal = OPERATING_SYSTEM_MX_BEAN.getTotalMemorySize();
        final long memoryFree = OPERATING_SYSTEM_MX_BEAN.getFreeMemorySize();

        final UsageInfo ramUsageInfo = new UsageInfo(
                "RAM",
                memoryTotal,
                memoryTotal - memoryFree,
                memoryFree);
        map.put(ramUsageInfo.path(), ramUsageInfo);

        final long swapTotal = OPERATING_SYSTEM_MX_BEAN.getTotalSwapSpaceSize();
        final long swapFree = OPERATING_SYSTEM_MX_BEAN.getFreeSwapSpaceSize();

        final UsageInfo swapUsageInfo = new UsageInfo(
                "SWAP",
                swapTotal,
                swapTotal - swapFree,
                swapFree);
        map.put(swapUsageInfo.path(), swapUsageInfo);

        return map;
    }

    @Override
    public long getTotalSystemMemory() {
        return OPERATING_SYSTEM_MX_BEAN.getTotalMemorySize();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected List<String> readContent(final ProcessBuilder processBuilder) {
        List<String> lines = null;
        List<String> errors = null;

        // .redirectErrorStream(true); // Gibt Fehler auf dem InputStream aus.
        try (Process process = processBuilder.start()) {
            try (Reader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                 Reader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                // lines = inputReader.lines().toList();
                // errors = errorReader.lines().toList();
                lines = inputReader.readAllLines();
                errors = errorReader.readAllLines();
            }

            process.waitFor();
        }
        catch (final InterruptedException ex) {
            getLogger().error(ex.getMessage());

            Thread.currentThread().interrupt();
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        if (!errors.isEmpty() && getLogger().isErrorEnabled()) {
            getLogger().error("'{}': {}", processBuilder.command(), String.join(System.lineSeparator(), errors));
        }

        return lines;
    }

    protected List<String> readContent(final String fileName) {
        return readContent(fileName, StandardCharsets.UTF_8);
    }

    protected List<String> readContent(final String fileName, final Charset charset) {
        final Path path = Paths.get(fileName);

        if (Files.notExists(path)) {
            return Collections.emptyList();
        }

        try {
            return Files.readAllLines(path, charset);

            // lines = Files.lines(path, charset).collect(Collectors.toList());

            // lines = new ArrayList<>();
            //
            // try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            // for (;;) {
            // String line = reader.readLine();
            //
            // if (line == null) {
            // break;
            // }
            //
            // lines.add(line);
            // }
            // }
            //
            // return lines;
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
