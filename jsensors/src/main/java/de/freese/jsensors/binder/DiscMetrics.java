// Created: 03.09.2021
package de.freese.jsensors.binder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class DiscMetrics implements SensorBinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscMetrics.class);

    private static String sanitizePostfix(final String postfix) {
        String fix = postfix.replace("-", ".");
        fix = fix.replace(" ", ".");
        fix = fix.replace("/", ".");
        fix = fix.replace("\\", ".");

        return fix;
    }

    private final File file;
    private final Path path;
    private final String sensorPostfix;

    public DiscMetrics(final String sensorPostfix, final File file) {
        super();

        this.sensorPostfix = Objects.requireNonNull(sensorPostfix, "sensorPostfix required");
        this.file = Objects.requireNonNull(file, "file required");

        path = null;
    }

    public DiscMetrics(final String sensorPostfix, final Path path) {
        super();

        this.sensorPostfix = Objects.requireNonNull(sensorPostfix, "sensorPostfix required");
        this.path = Objects.requireNonNull(path, "path required");

        file = null;
    }

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        if (file != null) {
            return bindTo(registry, file, File::getFreeSpace, File::getTotalSpace, backendProvider);
        }
        else if (path != null) {
            try {
                final FileStore fileStore = Files.getFileStore(path);

                return bindTo(registry, fileStore, fs -> {
                    try {
                        return fs.getUsableSpace();
                    }
                    catch (Exception ex) {
                        getLogger().error(ex.getMessage(), ex);
                    }

                    return 0L;
                }, fs -> {
                    try {
                        return fs.getTotalSpace();
                    }
                    catch (Exception ex) {
                        getLogger().error(ex.getMessage(), ex);
                    }

                    return 0L;
                }, backendProvider);
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        getLogger().warn("bound no sensors");

        return Collections.emptyList();
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    private <T> List<String> bindTo(final SensorRegistry registry, final T object, final ToLongFunction<T> functionFree, final ToLongFunction<T> functionTotal,
                                    final Function<String, Backend> backendProvider) {
        final String postfix = sanitizePostfix(sensorPostfix);

        Sensor.builder("disk.free." + postfix, object, obj -> {
            final long free = functionFree.applyAsLong(obj);

            return Long.toString(free);
        }).description("Free Disk-Space in Bytes").register(registry, backendProvider);

        Sensor.builder("disk.usage." + postfix, object, obj -> {
            final double free = functionFree.applyAsLong(obj);
            final long total = functionTotal.applyAsLong(obj);

            if (total == 0L) {
                return "0";
            }

            final double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used Disk-Space in %").register(registry, backendProvider);

        return List.of("disk.free." + postfix, "disk.usage." + postfix);
    }
}
