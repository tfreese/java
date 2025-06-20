// Created: 05.12.2020
package de.freese.jconky.painter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.Values;

/**
 * @author Thomas Freese
 */
public class CpuMonitorPainter extends AbstractMonitorPainter {
    private final Map<Integer, Values<Double>> coreUsageMap = new HashMap<>();
    private final Stop[] gradientStops;

    public CpuMonitorPainter() {
        super();

        gradientStops = new Stop[]{new Stop(0D, getSettings().getColorGradientStart()), new Stop(1D, getSettings().getColorGradientStop())};
    }

    @Override
    public double paintValue(final GraphicsContext gc, final double width) {
        final CpuInfos cpuInfos = getContext().getCpuInfos();

        coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).addValue(cpuInfos.getTotal().getCpuUsage());

        for (int i = 0; i < getContext().getNumberOfCores(); i++) {
            coreUsageMap.computeIfAbsent(i, key -> new Values<>()).addValue(cpuInfos.get(i).getCpuUsage());
        }

        double y = paintTotal(gc, width, cpuInfos);

        gc.save();
        gc.translate(0, y);
        y += paintCores(gc, width, cpuInfos);
        gc.restore();

        final double height = y - 10D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    private double paintCore(final GraphicsContext gc, final double width, final CpuInfo cpuInfo) {
        final double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        final int core = cpuInfo.getCore();
        final double usage = cpuInfo.getCpuUsage();
        final int frequency = cpuInfo.getFrequency() / 1000;
        // final double temperature = cpuInfo.getTemperature();

        final String text;

        // if (temperature > 0D) {
        // text = String.format("Core%d%3.0f%% %4dMHz %2.0f°C", core, usage * 100D, frequency, temperature);
        // }
        // else {
        text = String.format("Core%02d %3.0f%% %4dMhz", core, usage * 100D, frequency);
        // }

        paintText(gc, text, x, y);

        x = fontSize * 12D;
        y = -fontSize + 3D;
        final double barWidth = width - x;

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y, barWidth, 10D);

        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.fillRect(x, y, usage * barWidth, 10D);

        return fontSize * 1.25D;
    }

    private double paintCores(final GraphicsContext gc, final double width, final CpuInfos cpuInfos) {
        final double fontSize = getSettings().getFontSize();

        final double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;

        final double coreWidth = width - getSettings().getMarginInner().getLeft() - getSettings().getMarginInner().getRight();

        for (int i = 0; i < getContext().getNumberOfCores(); i++) {
            gc.save();
            gc.translate(x, y);
            y += paintCore(gc, coreWidth, cpuInfos.get(i));
            gc.restore();
        }

        return y;
    }

    private double paintTotal(final GraphicsContext gc, final double width, final CpuInfos cpuInfos) {
        final CpuLoadAvg cpuLoadAvg = getContext().getCpuLoadAvg();

        final double fontSize = getSettings().getFontSize();

        gc.setFont(getSettings().getFont());

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, "CPU", x, y, width);

        // CpuLoads
        x = getSettings().getMarginInner().getLeft();
        y += fontSize + 5D;
        paintText(gc, String.format("Total: %.0f°C", cpuInfos.getTotal().getTemperature()), x, y);

        x = width - (fontSize * 13D);
        paintTextAndValue(gc, "Loads:", String.format("%.2f %.2f %.2f", cpuLoadAvg.getOneMinute(), cpuLoadAvg.getFiveMinutes(), cpuLoadAvg.getFifteenMinutes()), x, y);

        // CpuUsage Bar
        x = getSettings().getMarginInner().getLeft();
        y += 15D;

        gc.save();
        gc.translate(x, y);
        y += paintTotalBar(gc, width - x - getSettings().getMarginInner().getRight(), cpuInfos);
        gc.restore();

        // CpuUsage Graph
        x = getSettings().getMarginInner().getLeft();
        y -= fontSize;

        gc.save();
        gc.translate(x, y);
        y += paintTotalGraph(gc, width - x - getSettings().getMarginInner().getRight());
        gc.restore();

        return y;
    }

    private double paintTotalBar(final GraphicsContext gc, final double width, final CpuInfos cpuInfos) {
        final double height = 15D;
        final double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        final double usage = cpuInfos.getTotal().getCpuUsage();
        // double temperature = cpuInfos.getTotal().getTemperature();
        //
        // paintTextValue(gc, String.format("%3.0f%% %2.0f°C", usage * 100D, temperature), x, y);
        paintTextValue(gc, String.format("%3.0f%% ", usage * 100D), x, y);

        x += 40D;
        y += 3D;
        final double barWidth = width - x;

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y - fontSize, barWidth, 10D);

        y -= fontSize;
        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.fillRect(x, y, usage * barWidth, 10D);

        return height;
    }

    private double paintTotalGraph(final GraphicsContext gc, final double width) {
        final Values<Double> values = coreUsageMap.computeIfAbsent(-1, key -> new Values<>());
        final List<Double> valueList = values.getLastValues((int) width);
        final double height = 20D;

        // double minValue = 0D;
        // double maxValue = values.getMaxValue();
        // double minNorm = 0D;
        // double maxNorm = height - 2;

        // gc.setFill(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.setStroke(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, gradientStops));

        final double xOffset = width - valueList.size(); // Diagramm von rechts aufbauen.
        // final double xOffset = 0D; // Diagramm von links aufbauen.

        for (int i = 0; i < valueList.size(); i++) {
            final double value = valueList.get(i);

            final double x = i + xOffset;
            final double y = value * (height - 2);
            // final double y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

            // gc.fillRect(x, height - 1 - y, 1, y);
            gc.strokeLine(x, height - 1 - y, x, height - 1);
        }

        return height;
    }
}
