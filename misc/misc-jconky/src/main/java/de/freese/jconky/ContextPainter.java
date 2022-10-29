// Created: 04.12.2020
package de.freese.jconky;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.jconky.painter.MonitorPainter;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class ContextPainter
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextPainter.class);
    /**
     *
     */
    private final List<MonitorPainter> painters = new ArrayList<>();
    /**
     *
     */
    private Canvas canvas;
    /**
     *
     */
    private GraphicsContext gc;

    /**
     * @param monitorPainter {@link MonitorPainter}
     */
    public void addMonitorPainter(final MonitorPainter monitorPainter)
    {
        this.painters.add(monitorPainter);
    }

    /**
     *
     */
    public void paint()
    {
        getLogger().debug("paint");

        double width = this.canvas.getWidth();
        double height = this.canvas.getHeight();

        this.gc.clearRect(0, 0, width, height);
        this.gc.save();

        Insets marginOuter = Settings.getInstance().getMarginOuter();
        this.gc.translate(marginOuter.getLeft(), marginOuter.getTop());

        double monitorWidth = width - (marginOuter.getRight() * 2D);
        // double totalY = 0D;

        for (MonitorPainter painter : this.painters)
        {
            double monitorHeight = painter.paintValue(this.gc, monitorWidth);

            this.gc.translate(0D, monitorHeight);

            // totalY += monitorHeight;
        }

        // Koordinatenursprung wieder nach oben links verlegen um es komplett malen zu lassen.
        // this.gc.translate(-marginOuter.getLeft(), -totalY - marginOuter.getTop());
        this.gc.restore();
    }

    /**
     * @param canvas {@link Canvas}
     */
    public void setCanvas(final Canvas canvas)
    {
        this.canvas = Objects.requireNonNull(canvas, "canvas required");
        this.gc = Objects.requireNonNull(canvas.getGraphicsContext2D(), "graphicsContext required");

        // Font-Antialiasing, Gray = Default
        // this.gc.setFontSmoothingType(FontSmoothingType.LCD);
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }
}
