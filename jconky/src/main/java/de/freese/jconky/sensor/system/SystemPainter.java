package de.freese.jconky.sensor.system;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.sensor.AbstractSensorPainter;
import de.freese.jconky.util.JConkyUtils;

/**
 * @author Thomas Freese
 * @since 28.09.26
 */
public final class SystemPainter extends AbstractSensorPainter {
    private final Stop[] gradientStops;
    private final SystemSensor systemSensor;

    public SystemPainter(final SystemSensor systemSensor) {
        super();

        this.systemSensor = Objects.requireNonNull(systemSensor, "systemSensor required");

        gradientStops = new Stop[]{new Stop(0D, getSettings().getColorGradientStart()), new Stop(1D, getSettings().getColorGradientStop())};
    }

    @Override
    public double repaint(final GraphicsContext gc, final double startX, final double startY, final double width) {
        final Map<String, UsageInfo> usages = systemSensor.getUsages();

        final double fontSize = getSettings().getFontSize();

        final double x = getSettings().getMarginInner().getLeft();
        double y = startY + fontSize;
        paintTitle(gc, "System", x, y, width);

        final List<String> paths = Arrays.asList("RAM", "SWAP", "/", "/tmp");
        // final List<String> paths = Arrays.asList("RAM", "RAM1", "SWAP", "SWAP1", "/", "/tmp");

        for (final String path : paths) {
            y += fontSize * 1.25D;

            paintUsage(gc, startX, y, width - x - getSettings().getMarginInner().getRight(), usages.get(path));
        }

        // y += fontSize * 1.25D;
        // paintTextAndValue(gc, "Updates:", Integer.toString(getContext().getUpdates()), x, y);

        drawDebugBorder(gc, startX, startY, width, y);

        return y;
    }

    private void paintUsage(final GraphicsContext gc, final double startX, final double startY, final double width, final UsageInfo usageInfo) {
        if (usageInfo == null) {
            return;
        }

        final String path = String.format("%5s:", usageInfo.path());

        final String format = "%.1f%s";
        final long used = usageInfo.used();
        final long total = usageInfo.total();
        final double usage = usageInfo.getUsage();
        final String value = String.format("%s/%s", JConkyUtils.toHumanReadableSize(used, format), JConkyUtils.toHumanReadableSize(total, format));
        paintTextAndValue(gc, path, value, startX, startY);

        paintText(gc, String.format("%4.1f%%", usage * 100D), startX + 150D, startY);

        final double x = startX + 190D;

        final double barHeight = 9.5D;
        final double barWidth = width - 190D;

        // Horizontaler Gradient.
        gc.setFill(new LinearGradient(x, startY - barHeight, x + barWidth, startY - barHeight, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.fillRect(x, startY - barHeight, usage * barWidth, 10D);

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, startY - barHeight, barWidth, 10D);
    }
}
