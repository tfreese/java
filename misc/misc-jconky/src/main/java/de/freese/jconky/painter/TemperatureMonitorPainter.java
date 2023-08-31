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
    @Override
    public double paintValue(final GraphicsContext gc, final double width) {
        Map<String, TemperatureInfo> temperatures = getContext().getTemperatures();

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, "Temperatures", x, y, width);

        y += fontSize * 1.25D;
        StringBuilder sb = new StringBuilder("HDD: ");
        sb.append(String.format("sda %.0f°C", temperatures.get("/dev/sda").getTemperature()));
        sb.append(String.format("sdb %.0f°C", temperatures.get("/dev/sdb").getTemperature()));

        paintText(gc, sb.toString(), x, y);

        y += fontSize * 1.25D;
        sb = new StringBuilder("M2 : ");
        sb.append(String.format("980 %.0f°C", temperatures.get("/dev/nvme0n1").getTemperature()));
        paintText(gc, sb.toString(), x, y);

        y += fontSize * 1.25D;
        GpuInfo gpuInfo = (GpuInfo) temperatures.get("GPU");
        sb = new StringBuilder("GPU: ");
        sb.append(String.format("%.0f°C", gpuInfo.getTemperature()));
        sb.append(String.format(", %.2fW", gpuInfo.getPower()));
        sb.append(String.format(", Fan %d%%", gpuInfo.getFanSpeed()));
        sb.append(String.format(", Load %d%%", gpuInfo.getUsage()));
        paintText(gc, sb.toString(), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
