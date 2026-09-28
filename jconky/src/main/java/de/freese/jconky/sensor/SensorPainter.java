package de.freese.jconky.sensor;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
@FunctionalInterface
public interface SensorPainter {
    double repaint(GraphicsContext gc, double startX, double startY, double width);
}
