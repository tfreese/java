// Created: 04.12.2020
package de.freese.jconky;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jconky.painter.MonitorPainter;

/**
 * @author Thomas Freese
 */
public class ContextPainter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextPainter.class);

    private final List<MonitorPainter> painters = new ArrayList<>();

    private Canvas canvas;
    private GraphicsContext gc;

    public void addMonitorPainter(final MonitorPainter monitorPainter) {
        painters.add(monitorPainter);
    }

    public void paint() {
        getLogger().debug("paint");

        final double width = canvas.getWidth();
        final double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.save();

        final Insets marginOuter = Settings.getInstance().getMarginOuter();
        gc.translate(marginOuter.getLeft(), marginOuter.getTop());

        final double monitorWidth = width - (marginOuter.getRight() * 2D);
        // double totalY = 0D;

        for (MonitorPainter painter : painters) {
            final double monitorHeight = painter.paintValue(gc, monitorWidth);

            gc.translate(0D, monitorHeight);

            // totalY += monitorHeight;
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

    protected Logger getLogger() {
        return LOGGER;
    }
}
