package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.Utils;

/**
 * {@link Backend} for a rrdtool-File, only for Linux available<br>
 * Every {@link Sensor} has its own file.<br>
 *
 * @author Thomas Freese
 * @since 31.05.2017
 */
public class RrdToolBackend extends AbstractBatchBackend {
    /**
     * System.getProperty("line.separator")
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static class Builder {
        private int batchSize;
        private Path path;

        Builder() {
            super();
        }

        public Builder batchSize(final int batchSize) {
            this.batchSize = batchSize;

            return this;
        }

        public RrdToolBackend build() throws IOException {
            Objects.requireNonNull(path, "path required");

            if (batchSize < 1) {
                throw new IllegalArgumentException("batchSize < 1: " + batchSize);
            }

            // Create Directories.
            final Path parent = path.getParent();
            Files.createDirectories(parent);

            if (!Files.exists(path)) {
                LoggerFactory.getLogger(RrdToolBackend.Builder.class).info("create file: {}", path);

                // Create default RRD.
                final List<String> command = new ArrayList<>();
                command.add("rrdtool");
                command.add("create");
                command.add(path.toString());
                command.add("--step");
                command.add("60");
                command.add("DS:value_gauge:GAUGE:600:0:U");
                command.add("RRA:MIN:0.5:60:168");
                command.add("RRA:MAX:0.5:60:168");
                command.add("RRA:AVERAGE:0.5:1:10080");

                final List<String> lines = Utils.executeCommand(command);

                if (!lines.isEmpty()) {
                    throw new IOException(String.join(LINE_SEPARATOR, lines));
                }
            }

            return new RrdToolBackend(batchSize, path);
        }

        public Builder path(final Path path) {
            this.path = path;

            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Path path;

    RrdToolBackend(final int batchSize, final Path path) {
        super(batchSize);

        this.path = path;
    }

    @Override
    protected void storeValues(final List<SensorValue> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        try {
            for (final SensorValue sensorValue : values) {
                // Update RRD.
                final List<String> command = new ArrayList<>();
                command.add("rrdtool");
                command.add("update");
                command.add(path.toString());
                command.add(String.format("%s:%s", sensorValue.timestamp(), sensorValue.value()));

                final List<String> lines = Utils.executeCommand(command);

                if (!lines.isEmpty()) {
                    throw new IOException(String.join(LINE_SEPARATOR, lines));
                }
            }
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
