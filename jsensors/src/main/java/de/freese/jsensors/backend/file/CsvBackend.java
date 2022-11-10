// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;

/**
 * {@link Backend} for a CSV-File.<br>
 *
 * @author Thomas Freese
 */
public class CsvBackend extends AbstractBatchBackend implements LifeCycle
{
    private final boolean exclusive;

    private final Path path;

    /**
     * @param exclusive boolean; File exclusive for only one {@link Sensor} -> no column 'NAME'
     */
    public CsvBackend(final Path path, final boolean exclusive, final int batchSize)
    {
        super(batchSize);

        this.path = Objects.requireNonNull(path, "path required");
        this.exclusive = exclusive;
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

                // Create CSV-Header
                try (OutputStream os = Files.newOutputStream(this.path, StandardOpenOption.CREATE))
                {
                    String header = null;

                    if (this.exclusive)
                    {
                        // Without SensorName.
                        header = String.format("\"%s\",\"%s\",\"%s\"%n", "VALUE", "TIMESTAMP", "TIME");
                    }
                    else
                    {
                        // With SensorName.
                        header = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n", "NAME", "VALUE", "TIMESTAMP", "TIME");
                    }

                    byte[] bytes = header.getBytes(StandardCharsets.UTF_8);

                    os.write(bytes);
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

    protected byte[] encode(final SensorValue sensorValue)
    {
        String formatted = null;

        if (this.exclusive)
        {
            // Ohne SensorName.
            formatted = String.format("\"%s\",\"%d\",\"%s\"%n", sensorValue.getValue(), sensorValue.getTimestamp(), sensorValue.getLocalDateTime());
        }
        else
        {
            // Mit SensorName.
            formatted =
                    String.format("\"%s\",\"%s\",\"%d\",\"%s\"%n", sensorValue.getName(), sensorValue.getValue(), sensorValue.getTimestamp(), sensorValue.getLocalDateTime());
        }

        return formatted.getBytes(StandardCharsets.UTF_8);
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

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(this.path, StandardOpenOption.APPEND)))
        {
            for (SensorValue sensorValue : values)
            {
                byte[] bytes = encode(sensorValue);

                os.write(bytes);
            }

            os.flush();
        }
        catch (Exception ex)
        {
            // throw new UncheckedIOException(ex);
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
