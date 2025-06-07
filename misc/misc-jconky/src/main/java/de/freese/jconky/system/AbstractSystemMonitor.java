// Created: 01.12.2020
package de.freese.jconky.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractSystemMonitor implements SystemMonitor {
    /**
     * "[ ]" = "\\s+" = Whitespace: einer oder mehrere
     */
    protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long myPid;

    protected AbstractSystemMonitor() {
        super();

        this.myPid = ProcessHandle.current().pid();
    }

    /**
     * Liefert die eigene Process-ID
     */
    public long getMyPid() {
        return myPid;
    }

    @Override
    public int getNumberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public long getTotalSystemMemory() {
        return OPERATING_SYSTEM_MX_BEAN.getTotalMemorySize();
    }

    public void setMyPid(final long myPid) {
        this.myPid = myPid;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected List<String> readContent(final ProcessBuilder processBuilder) {
        List<String> lines = null;
        List<String> errors = null;

        try {
            final Process process = processBuilder.start();

            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                lines = inputReader.lines().toList();
                errors = errorReader.lines().toList();
            }

            try {
                process.waitFor();
            }
            catch (InterruptedException ex) {
                getLogger().error(ex.getMessage());

                Thread.currentThread().interrupt();
            }

            process.destroy();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        if (errors != null && !errors.isEmpty()) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("'{}': {}", processBuilder.command(), String.join(System.lineSeparator(), errors));
            }
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
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
