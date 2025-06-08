// Created: 31.05.2021
package de.freese.micrometer.binder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class NetworkMetrics implements MeterBinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMetrics.class);

    private final List<String> activeInterfaces = new ArrayList<>();
    private final Iterable<Tag> tags;

    public NetworkMetrics() {
        this(Collections.emptyList());
    }

    public NetworkMetrics(final Iterable<Tag> tags) {
        super();

        this.tags = Objects.requireNonNull(tags, "tags required");

        try {
            activeInterfaces.addAll(getActiveInterfaces());
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void bindTo(final MeterRegistry registry) {
        // Mit StepCounter müsste die Differenz aus altem und neuem Wert gesetzt werden.
        // Der StepFunctionCounter berechnet die Differenz automatisch.
        // Die Reihenfolge der Meter-Abfrage ergibt sich aus deren Reihenfolge der Registrierung.
        activeInterfaces.forEach(iface -> {
            final NetworkInterface networkInterface = new NetworkInterface(iface);

            FunctionCounter.builder("network.in", networkInterface, NetworkInterface::getInput)
                    .tags(tags).tag("interface", iface)
                    .description("Network Input for " + iface)
                    .baseUnit(BaseUnits.BYTES)
                    .register(registry);

            FunctionCounter.builder("network.out", networkInterface, NetworkInterface::getOutput)
                    .tags(tags).tag("interface", iface)
                    .description("Network Output for " + iface)
                    .baseUnit(BaseUnits.BYTES)
                    .register(registry);
        });
    }

    private List<String> getActiveInterfaces() throws IOException {
        List<Path> interfacePaths = null;

        try (Stream<Path> interfaces = Files.list(Paths.get("/sys/class/net/"))) {
            interfacePaths = interfaces.toList();
        }

        final List<String> interfaces = new ArrayList<>();

        for (Path interfacePath : interfacePaths) {
            final Path pathState = interfacePath.resolve("operstate");

            final String state = Files.readString(pathState);

            if ("up".equals(state.strip())) {
                interfaces.add(interfacePath.getFileName().toString());
            }
        }

        return interfaces;
    }

    // private void update() {
    // for (String iface : activeInterfaces) {
    // try {
    // final List<String> lines = executeCommand("ifconfig", iface);
    //
    // // lines.stream().forEach(System.out::println);
    // // lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("RX packets")).forEach(System.out::println);
    // // lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("TX packets")).forEach(System.out::println);
    //
    // // ArchLinux:
    // // RX packets 32997 bytes 46685918 (44.5 MiB)
    // // TX packets 15894 bytes 1288395 (1.2 MiB)
    // // long inputOld = input;
    //
    // final long input = lines.stream().map(String::trim).filter(l -> l.startsWith("RX packets")).mapToLong(l -> {
    // final Matcher matcher = PATTERN_BYTES.matcher(l);
    //
    // matcher.find();
    // final long value = Long.parseLong(matcher.group(1));
    // return value;
    // }).findFirst().orElse(0L);
    //
    // Metrics.globalRegistry.find("network.in").tag("interface", iface).counter().increment(input);
    //
    // final long output = lines.stream().map(String::trim).filter(l -> l.startsWith("TX packets")).mapToLong(l -> {
    // final Matcher matcher = PATTERN_BYTES.matcher(l);
    //
    // matcher.find();
    // final long value = Long.parseLong(matcher.group(1));
    // return value;
    // }).findFirst().orElse(0L);
    //
    // Metrics.globalRegistry.find("network.out").tag("interface", iface).counter().increment(output);
    //
    // // System.out.printf("NetworkMetrics.update(): Input=%d, Output=%d", input, output);
    // }
    // catch (Exception ex) {
    // LOGGER.error(ex.getMessage(), ex);
    // }
    // }
    // }
}
