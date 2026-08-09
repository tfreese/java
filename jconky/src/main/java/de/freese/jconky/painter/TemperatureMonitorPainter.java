// Created: 23.12.2020
package de.freese.jconky.painter;

import java.util.Map;

import javafx.scene.canvas.GraphicsContext;

import de.freese.jconky.model.GpuInfo;
import de.freese.jconky.model.TemperatureInfo;

/**
 * @author Thomas Freese
 */
public class TemperatureMonitorPainter extends AbstractMonitorPainter {
    private static final TemperatureInfo TEMPERATURE_INFO = new TemperatureInfo();

    @Override
    public double paintValue(final GraphicsContext gc, final double width) {
        final Map<String, TemperatureInfo> temperatures = getContext().getTemperatures();

        final double fontSize = getSettings().getFontSize();

        final double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, "Temperatures", x, y, width);

        y += fontSize * 1.25D;
        StringBuilder sb = new StringBuilder("HDD: ");
        sb.append(String.format("sda %.0f°C", temperatures.getOrDefault("/dev/sda", TEMPERATURE_INFO).getTemperature()));
        sb.append(String.format("sdb %.0f°C", temperatures.getOrDefault("/dev/sdb", TEMPERATURE_INFO).getTemperature()));

        paintText(gc, sb.toString(), x, y);

        y += fontSize * 1.25D;
        sb = new StringBuilder("M2 : ");
        sb.append(String.format("980 %.0f°C", temperatures.getOrDefault("/dev/nvme0n1", TEMPERATURE_INFO).getTemperature()));
        paintText(gc, sb.toString(), x, y);

        y += fontSize * 1.25D;
        final GpuInfo gpuInfo = (GpuInfo) temperatures.getOrDefault("GPU", TEMPERATURE_INFO);
        sb = new StringBuilder("GPU: ");
        sb.append(String.format("%.0f°C", gpuInfo.getTemperature()));
        sb.append(String.format(", %.2fW", gpuInfo.getPower()));
        sb.append(String.format(", Fan %d%%", gpuInfo.getFanSpeed()));
        sb.append(String.format(", Load %d%%", gpuInfo.getUsage()));
        paintText(gc, sb.toString(), x, y);

        final double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
