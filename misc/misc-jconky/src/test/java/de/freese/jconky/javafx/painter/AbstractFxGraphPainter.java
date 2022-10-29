// Created: 27.11.2020
package de.freese.jconky.javafx.painter;

import de.freese.jconky.model.Values;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFxGraphPainter
{
    /**
     *
     */
    private final Values<Float> values = new Values<>();

    /**
     * Erstellt ein neues {@link AbstractFxGraphPainter} Object.
     */
    protected AbstractFxGraphPainter()
    {
        super();
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width int
     * @param height int
     */
    protected void configureBackground(final GraphicsContext gc, final double width, final double height)
    {
        gc.clearRect(0, 0, width, height);
    }

    /**
     * @return {@link Values}<Float>
     */
    public Values<Float> getValues()
    {
        return this.values;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    public void paint(final GraphicsContext gc, final double width, final double height)
    {
        configureBackground(gc, width, height);

        paintGraph(gc, width, height);

        // g.dispose(); // Dispose nur wenn man es selbst erzeugt hat.
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    public abstract void paintGraph(final GraphicsContext gc, final double width, final double height);
}
