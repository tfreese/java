// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
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
public class RrdToolBackend extends AbstractBatchBackend implements LifeCycle
{
    /**
     *
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     *
     */
    private final Path path;

    /**
     * Erstellt ein neues {@link RrdToolBackend} Object.
     *
     * @param path {@link Path}
     * @param batchSize int
     */
    public RrdToolBackend(final Path path, final int batchSize)
    {
        super(batchSize);

        this.path = Objects.requireNonNull(path, "path required");
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        try
        {
            // Create Directories.
            Path parent = this.path.getParent();
            Files.createDirectories(parent);

            if (!Files.exists(this.path))
            {
                getLogger().info("create file: {}", this.path);

                // Create default RRD.
                List<String> command = new ArrayList<>();
                command.add("rrdtool");
                command.add("create");
                command.add(this.path.toString());
                command.add("--step");
                command.add("60");
                command.add("DS:value_gauge:GAUGE:600:0:U");
                command.add("RRA:MIN:0.5:60:168");
                command.add("RRA:MAX:0.5:60:168");
                command.add("RRA:AVERAGE:0.5:1:10080");

                List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

                if (!lines.isEmpty())
                {
                    throw new IOException(String.join(LINE_SEPARATOR, lines));
                }
            }
        }
        catch (Exception ex)
        {
            // throw new UncheckedIOException(ex);
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        submit();
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBatchBackend#storeValues(java.util.List)
     */
    @Override
    protected void storeValues(final List<SensorValue> values)
    {
        if ((values == null) || values.isEmpty())
        {
            return;
        }

        try
        {
            for (SensorValue sensorValue : values)
            {
                // Update RRD.
                List<String> command = new ArrayList<>();
                command.add("rrdtool");
                command.add("update");
                command.add(this.path.toString());
                command.add(String.format("%s:%s", sensorValue.getTimestamp(), sensorValue.getValue()));

                List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

                if (!lines.isEmpty())
                {
                    throw new IOException(String.join(LINE_SEPARATOR, lines));
                }
            }
        }
        catch (Exception ex)
        {
            // throw new UncheckedIOException(ex);
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
