// Created: 13.12.2020
package de.freese.jconky.painter;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface MonitorPainter {
    double paintValue(GraphicsContext gc, double width);
}
