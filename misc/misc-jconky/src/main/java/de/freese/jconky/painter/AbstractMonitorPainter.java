// Created: 13.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.Context;
import de.freese.jconky.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMonitorPainter implements MonitorPainter
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void drawDebugBorder(final GraphicsContext gc, final double width, final double height)
    {
        if (getSettings().isDebug())
        {
            // gc.setLineDashes();
            gc.setStroke(Color.RED);
            gc.strokeRect(0, 0, width, height);
        }
    }

    protected Context getContext()
    {
        return Context.getInstance();
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    protected Settings getSettings()
    {
        return Settings.getInstance();
    }

    protected void paintText(final GraphicsContext gc, final String text, final double x, final double y)
    {
        gc.setFill(getSettings().getColorText());
        gc.fillText(text, x, y);
    }

    protected void paintTextAndValue(final GraphicsContext gc, final String text, final String value, final double x, final double y)
    {
        paintText(gc, text, x, y);

        int length = text.length();
        length++;

        double lengthFactor = 6.7D;

        if (length >= 15)
        {
            lengthFactor = 6.9D;
        }
        else if (length >= 8)
        {
            lengthFactor = 6.8D;
        }

        paintTextValue(gc, value, x + (length * lengthFactor), y);
    }

    protected void paintTextValue(final GraphicsContext gc, final String value, final double x, final double y)
    {
        gc.setFill(getSettings().getColorValue());
        gc.fillText(value, x, y);
    }

    protected void paintTitle(final GraphicsContext gc, final String title, final double x, final double y, final double width)
    {
        gc.setFill(getSettings().getColorTitle());
        gc.fillText(title, x, y);

        double fontSize = getSettings().getFontSize();
        int length = title.length();
        length++;

        double strokeY = (y - (fontSize / 2D)) + 1.5D;

        gc.setStroke(getSettings().getColorTitle());
        gc.setLineDashes(3D);
        gc.strokeLine(x + (length * 7.3D), strokeY, width - getSettings().getMarginInner().getRight(), strokeY);
        gc.setLineDashes();
    }
}
