package de.freese.jconky.painter;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 * @since 13.12.2020
 */
@FunctionalInterface
public interface MonitorPainter {
    double paintValue(GraphicsContext gc, double width);
}
