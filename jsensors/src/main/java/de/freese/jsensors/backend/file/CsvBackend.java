package de.freese.jsensors.backend.file;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * {@link Backend} for a CSV-File.<br>
 *
 * @author Thomas Freese
 * @since 31.05.2017
 */
public class CsvBackend extends AbstractBatchBackend {
    public static class Builder {
        private int batchSize;
        private boolean exclusive;
        private Path path;

        Builder() {
            super();
        }

        public Builder batchSize(final int batchSize) {
            this.batchSize = batchSize;

            return this;
        }

        public CsvBackend build() throws IOException {
            Objects.requireNonNull(path, "path required");

            if (batchSize < 1) {
                throw new IllegalArgumentException("batchSize < 1: " + batchSize);
            }

            // Create Directories.
            final Path parent = path.getParent();
            Files.createDirectories(parent);

            if (!Files.exists(path)) {
                LoggerFactory.getLogger(CsvBackend.Builder.class).info("create file: {}", path);

                // Create CSV-Header
                try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
                    final String header;

                    if (exclusive) {
                        // Without SensorName.
                        header = String.format("\"%s\",\"%s\",\"%s\"%n", "VALUE", "TIMESTAMP", "TIME");
                    }
                    else {
                        // With SensorName.
                        header = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n", "NAME", "VALUE", "TIMESTAMP", "TIME");
                    }

                    final byte[] bytes = header.getBytes(StandardCharsets.UTF_8);

                    os.write(bytes);
                }
            }

            return new CsvBackend(batchSize, path, exclusive);
        }

        /**
         * @param exclusive; true=One file for one sensor, false=One file for all sensors.
         */
        public Builder exclusive(final boolean exclusive) {
            this.exclusive = exclusive;

            return this;
        }

        public Builder path(final Path path) {
            this.path = path;

            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final boolean exclusive;
    private final Path path;

    /**
     * @param exclusive boolean; File exclusive for only one {@link Sensor} -> no column 'NAME'
     */
    CsvBackend(final int batchSize, final Path path, final boolean exclusive) {
        super(batchSize);

        this.path = path;
        this.exclusive = exclusive;
    }

    protected byte[] encode(final SensorValue sensorValue) {
        final String formatted;

        if (exclusive) {
            // Without Sensor Name.
            formatted = String.format("\"%s\",\"%d\",\"%s\"%n", sensorValue.value(), sensorValue.timestamp(), sensorValue.getLocalDateTime());
        }
        else {
            // With Sensor Name.
            formatted = String.format("\"%s\",\"%s\",\"%d\",\"%s\"%n", sensorValue.name(), sensorValue.value(), sensorValue.timestamp(), sensorValue.getLocalDateTime());
        }

        return formatted.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void storeValues(final List<SensorValue> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.APPEND))) {
            for (final SensorValue sensorValue : values) {
                final byte[] bytes = encode(sensorValue);

                os.write(bytes);
            }

            os.flush();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
