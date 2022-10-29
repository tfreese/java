// Created: 13.12.2020
package de.freese.jconky.painter;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface MonitorPainter
{
    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     *
     * @return double Höhe
     */
    double paintValue(GraphicsContext gc, final double width);
}
