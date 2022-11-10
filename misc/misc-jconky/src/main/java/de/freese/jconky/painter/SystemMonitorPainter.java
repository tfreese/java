// Created: 22.12.2020
package de.freese.jconky.painter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.util.JConkyUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class SystemMonitorPainter extends AbstractMonitorPainter
{
    private final Stop[] gradientStops;

    public SystemMonitorPainter()
    {
        super();

        this.gradientStops = new Stop[]
                {
                        new Stop(0D, getSettings().getColorGradientStart()), new Stop(1D, getSettings().getColorGradientStop())
                };
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        Map<String, UsageInfo> usages = getContext().getUsages();

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, "System", x, y, width);

        List<String> paths = Arrays.asList("RAM", "SWAP", "/", "/tmp");

        for (String path : paths)
        {
            y += fontSize * 1.25D;

            gc.save();
            gc.translate(x, y);
            paintUsage(gc, width - x - getSettings().getMarginInner().getRight(), usages.get(path));
            gc.restore();
        }

        y += fontSize * 1.25D;
        paintTextAndValue(gc, "Updates:", Integer.toString(getContext().getUpdates()), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    private void paintUsage(final GraphicsContext gc, final double width, final UsageInfo usageInfo)
    {
        if (usageInfo == null)
        {
            return;
        }

        double x = getSettings().getMarginInner().getLeft();
        double y = 0;

        String path = String.format("%7s:", usageInfo.getPath());

        String format = "%.1f%s";
        long used = usageInfo.getUsed();
        long size = usageInfo.getSize();
        double usage = usageInfo.getUsage();
        String value = String.format("%s/%s", JConkyUtils.toHumanReadableSize(used, format), JConkyUtils.toHumanReadableSize(size, format));
        paintTextAndValue(gc, path, value, x, y);

        x = 150D;
        paintTextValue(gc, String.format("%4.1f%%", usage * 100D), x, y);

        x = 190D;
        y = -9.5D;
        double barWidth = width - x;
        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y, barWidth, 10D);

        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.fillRect(x, y, usage * barWidth, 10D);
    }
}
