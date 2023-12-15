// Created: 26.01.2014
package de.freese.simulationen;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.simulationen.model.Simulation;
import de.freese.simulationen.model.SimulationListener;
import de.freese.simulationen.model.SimulationType;

/**
 * Speichert die Bilder der Simulation.
 *
 * @author Thomas Freese
 */
public class SimulationListenerSaveImage implements SimulationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationListenerSaveImage.class);

    /**
     * @author Thomas Freese
     */
    private final class WriteImageTask implements Runnable {
        private final BufferedImage bufferedImage;
        private final Path file;

        WriteImageTask(final BufferedImage bufferedImage, final Path file) {
            super();

            this.bufferedImage = Objects.requireNonNull(bufferedImage, "bufferedImage required");
            this.file = Objects.requireNonNull(file, "file required");
        }

        @Override
        public void run() {
            write(this.bufferedImage, this.file);
        }
    }

    private final AtomicInteger counter;
    private final Path directory;
    private final Executor executor;
    private final String format;
    private final SimulationType type;

    /**
     * @param format String; JPEG, PNG, BMP, WBMP, GIF
     */
    public SimulationListenerSaveImage(final String format, final Path directory, final SimulationType type, final Executor executor) {
        super();

        this.format = Objects.requireNonNull(format, "format required");
        this.directory = Objects.requireNonNull(directory, "directory required");
        this.type = Objects.requireNonNull(type, "type required");
        this.executor = Objects.requireNonNull(executor, "executor required");
        this.counter = new AtomicInteger(0);
    }

    @Override
    public void completed(final Simulation simulation) {
        final Image image = simulation.getImage();

        final BufferedImage bufferedImage = new BufferedImage(simulation.getWidth(), simulation.getHeight(), BufferedImage.TYPE_INT_RGB);

        final Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Liefert die gleiche Array-Referenz.
        // final int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        // Erzeugt ein neues Array.
        // final int[] pixels = bufferedImage.getRaster().getPixels(0, 0, width, height, (int[]) null);

        final Path file = this.directory.resolve(String.format("%s-%05d.%s", this.type.getNameShort(), this.counter.incrementAndGet(), this.format));

        this.executor.execute(new WriteImageTask(bufferedImage, file));
    }

    private void write(final BufferedImage bufferedImage, final Path file) {
        LOGGER.info("Write {}", file);

        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(file))) {
            // JPEG, PNG, BMP, WBMP, GIF
            ImageIO.write(bufferedImage, this.format, outputStream);

            outputStream.flush();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
