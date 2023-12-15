// Created: 30.06.2021
package de.freese.micrometer.binder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class NetworkInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkInterface.class);

    private static final Pattern PATTERN_BYTES = Pattern.compile(" bytes (.+?) ", Pattern.UNICODE_CHARACTER_CLASS);

    private final String iface;

    private double input;
    private double output;

    NetworkInterface(final String iface) {
        super();

        this.iface = Objects.requireNonNull(iface, "iface required");
    }

    /**
     * Die Reihenfolge der Meter-Abfrage ergibt sich aus deren Reihenfolge der Registrierung.
     */
    double getInput() {
        LOGGER.info("getInput: {}", this.iface);

        update();

        return this.input;
    }

    /**
     * Die Reihenfolge der Meter-Abfrage ergibt sich aus deren Reihenfolge der Registrierung.
     */
    double getOutput() {
        LOGGER.info("getOutput: {}", this.iface);

        return this.output;
    }

    /**
     * Ausführen eines OS-Commands über {@link Process}.<br>
     * Leerzeilen werden bei der Ausgabe entfernt.<br>
     * Bei Exceptions wird eine leere Liste geliefert.
     */
    private List<String> executeCommand(final String... command) {
        List<String> list = Collections.emptyList();

        try {
            // @formatter:off
            final Process process = new ProcessBuilder()
                    .command(command)
                    .redirectErrorStream(true)
                    .start()
                    ;
            // @formatter:on

            final Charset charset = StandardCharsets.UTF_8;

            // try (InputStreamReader isr = new InputStreamReader(process.getInputStream()))
            // {
            // System.out.println(isr.getEncoding());
            // }
            try (BufferedReader readerIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
                list = readerIn.lines().toList();
            }

            process.waitFor();
            process.destroy();
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return list;
    }

    private void update() {
        LOGGER.info("update: {}", this.iface);

        final List<String> lines = executeCommand("ifconfig", this.iface).stream().map(String::strip).filter(line -> !line.isEmpty()).toList();

        this.input = lines.stream().filter(l -> l.startsWith("RX packets")).mapToLong(l -> {
            final Matcher matcher = PATTERN_BYTES.matcher(l);
            matcher.find();

            return Long.parseLong(matcher.group(1));
        }).findFirst().orElse(0L);

        this.output = lines.stream().filter(l -> l.startsWith("TX packets")).mapToLong(l -> {
            final Matcher matcher = PATTERN_BYTES.matcher(l);
            matcher.find();

            return Long.parseLong(matcher.group(1));
        }).findFirst().orElse(0L);
    }
}
