// Created: 03.09.2021
package de.freese.jsensors.binder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.ToLongFunction;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class DiscMetrics implements SensorBinder
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscMetrics.class);
    /**
     *
     */
    private final File file;
    /**
     *
     */
    private final Path path;
    /**
     *
     */
    private final String sensorPostfix;

    /**
     * Erstellt ein neues {@link DiscMetrics} Object.
     *
     * @param sensorPostfix String
     * @param file {@link File}
     */
    public DiscMetrics(final String sensorPostfix, final File file)
    {
        super();

        this.sensorPostfix = Objects.requireNonNull(sensorPostfix, "sensorPostfix required");
        this.file = Objects.requireNonNull(file, "file required");
        this.path = null;
    }

    /**
     * Erstellt ein neues {@link DiscMetrics} Object.
     *
     * @param sensorPostfix String
     * @param path {@link Path}
     */
    public DiscMetrics(final String sensorPostfix, final Path path)
    {
        super();

        this.sensorPostfix = Objects.requireNonNull(sensorPostfix, "sensorPostfix required");
        this.path = Objects.requireNonNull(path, "path required");
        this.file = null;
    }

    /**
     * @see de.freese.jsensors.binder.SensorBinder#bindTo(de.freese.jsensors.registry.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry)
    {
        if (this.file != null)
        {
            bindTo(registry, this.file, File::getFreeSpace, File::getTotalSpace);
        }
        else
        {
            try
            {
                FileStore fileStore = Files.getFileStore(this.path);

                bindTo(registry, fileStore, fs ->
                {
                    try
                    {
                        return fs.getUsableSpace();
                    }
                    catch (Exception ex)
                    {
                        getLogger().error(ex.getMessage(), ex);
                    }

                    return 0L;
                }, fs ->
                {
                    try
                    {
                        return fs.getTotalSpace();
                    }
                    catch (Exception ex)
                    {
                        getLogger().error(ex.getMessage(), ex);
                    }

                    return 0L;
                });
            }
            catch (IOException ex)
            {
                throw new UncheckedIOException(ex);
            }
        }
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @param <T> Type of the object from which the value is extracted.
     * @param registry {@link SensorRegistry}
     * @param object Object
     * @param functionFree {@link ToLongFunction}
     * @param functionTotal {@link ToLongFunction}
     */
    private <T> void bindTo(final SensorRegistry registry, final T object, final ToLongFunction<T> functionFree, final ToLongFunction<T> functionTotal)
    {
        String postfix = sanitizePostfix(this.sensorPostfix);

        Sensor.builder("disk.free." + postfix, object, obj ->
        {
            long free = functionFree.applyAsLong(obj);

            return Long.toString(free);
        }).description("Free Disk-Space in Bytes").register(registry);

        Sensor.builder("disk.usage." + postfix, object, obj ->
        {
            double free = functionFree.applyAsLong(obj);
            long total = functionTotal.applyAsLong(obj);

            if (total == 0L)
            {
                return "0";
            }

            double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used Disk-Space in %").register(registry);
    }

    /**
     * @param postfix String
     *
     * @return String
     */
    private String sanitizePostfix(final String postfix)
    {
        String fix = postfix.replace("-", ".");
        fix = fix.replace(" ", ".");
        fix = fix.replace("/", ".");
        fix = fix.replace("\\", ".");

        return fix;
    }
}
