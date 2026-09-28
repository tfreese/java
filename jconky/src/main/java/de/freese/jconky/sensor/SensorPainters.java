package de.freese.jconky.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jconky.Settings;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public final class SensorPainters {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorPainters.class);

    private final List<SensorPainter> painters = new ArrayList<>();

    private Canvas canvas;
    private GraphicsContext gc;

    public SensorPainters addSensorPainter(final SensorPainter sensorPainter) {
        painters.add(sensorPainter);

        return this;
    }

    public void repaint() {
        final double width = canvas.getWidth();
        final double height = canvas.getHeight();

        gc.clearRect(0D, 0D, width, height);
        gc.save();

        final Insets marginOuter = getSettings().getMarginOuter();
        gc.translate(marginOuter.getLeft(), marginOuter.getTop());

        final double monitorWidth = width - (marginOuter.getRight() * 2D);
        final double startX = getSettings().getMarginInner().getLeft();
        double totalY = 0D;

        for (final SensorPainter sensorPainter : painters) {
            try {
                totalY = sensorPainter.repaint(gc, startX, totalY, monitorWidth);
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            totalY += 5D;

            // gc.save();
            // gc.translate(0D, totalY);
            // gc.restore();
        }

        // Koordinatenursprung wieder nach oben links verlegen um es komplett malen zu lassen.
        // gc.translate(-marginOuter.getLeft(), -totalY - marginOuter.getTop());
        gc.restore();
    }

    public void setCanvas(final Canvas canvas) {
        this.canvas = Objects.requireNonNull(canvas, "canvas required");

        gc = Objects.requireNonNull(canvas.getGraphicsContext2D(), "graphicsContext required");

        // Font-Antialiasing, Gray = Default
        // gc.setFontSmoothingType(FontSmoothingType.LCD);
    }

    private Settings getSettings() {
        return Settings.getInstance();
    }
}
