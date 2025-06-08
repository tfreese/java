// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;
import de.freese.jsensors.utils.Utils;

/**
 * {@link Backend} for a rrdtool-File, only for Linux available<br>
 * Every {@link Sensor} has its own file.<br>
 *
 * @author Thomas Freese
 */
public class RrdToolBackend extends AbstractBatchBackend implements LifeCycle {
    /**
     * System.getProperty("line.separator")
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Path path;

    public RrdToolBackend(final int batchSize, final Path path) {
        super(batchSize);

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public void start() {
        try {
            // Create Directories.
            final Path parent = this.path.getParent();
            Files.createDirectories(parent);

            if (!Files.exists(path)) {
                getLogger().info("create file: {}", path);

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
                    throw new RuntimeException(String.join(LINE_SEPARATOR, lines));
                }
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        submit();
    }

    @Override
    protected void storeValues(final List<SensorValue> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        try {
            for (SensorValue sensorValue : values) {
                // Update RRD.
                final List<String> command = new ArrayList<>();
                command.add("rrdtool");
                command.add("update");
                command.add(path.toString());
                command.add(String.format("%s:%s", sensorValue.getTimestamp(), sensorValue.getValue()));

                final List<String> lines = Utils.executeCommand(command);

                if (!lines.isEmpty()) {
                    throw new RuntimeException(String.join(LINE_SEPARATOR, lines));
                }
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
