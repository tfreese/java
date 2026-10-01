package de.freese.jconky.sensor.cpu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import de.freese.jconky.sensor.AbstractSensorPainter;
import de.freese.jconky.sensor.Values;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public final class CpuPainter extends AbstractSensorPainter {
    private final Map<Integer, Values<Double>> coreUsageMap = new HashMap<>();

    private final CpuSensor cpuSensor;
    private final Stop[] gradientStops;

    public CpuPainter(final CpuSensor cpuSensor) {
        super();

        this.cpuSensor = Objects.requireNonNull(cpuSensor, "cpuSensor required");
        gradientStops = new Stop[]{new Stop(0D, getSettings().getColorGradientStart()), new Stop(1D, getSettings().getColorGradientStop())};
    }

    @Override
    public double repaint(final GraphicsContext gc, final double startX, final double startY, final double width) {
        final Cpu cpu = cpuSensor.getCpu();

        coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).addValue(cpu.getUsage());

        for (int i = 0; i < cpu.getNumberOfCores(); i++) {
            coreUsageMap.computeIfAbsent(i, key -> new Values<>()).addValue(cpu.getCore(i).usage());
        }

        double y = startY + getSettings().getFontSize();
        paintTitle(gc, "CPU", startX, y, width);

        y = paintTotal(gc, startX, y, width, cpu);

        // CpuUsage Bar
        y = paintTotalBar(gc, startX, y, width - startX - getSettings().getMarginInner().getRight(), cpu);

        // CpuUsage Graph
        y = paintTotalGraph(gc, startX, y, width - startX - getSettings().getMarginInner().getRight());

        y = paintCores(gc, startX, y, width, cpu);

        // // final double height = y - 10D;
        drawDebugBorder(gc, startX, startY, width, y);

        return y;
    }

    private double paintCore(final GraphicsContext gc, final double startX, final double startY, final double width, final CpuCore cpuCore) {
        final double fontSize = getSettings().getFontSize();

        double x = startX;
        final double y = startY + fontSize;

        final int core = cpuCore.core() + 1;
        final double usage = cpuCore.usage();
        final int frequency = cpuCore.frequency() / 1000;
        // final double temperature = cpuInfo.getTemperature();

        final String text = String.format("Core%02d %3.0f%% %4dMhz", core, usage * 100D, frequency);
        paintText(gc, text, x, y);

        x = fontSize * 12D;
        final double barWidth = width - x;

        gc.setFill(new LinearGradient(x, y - fontSize, x + barWidth, y - fontSize, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.fillRect(x, y - fontSize + 5D, usage * barWidth, 10D);

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y - fontSize + 5D, barWidth, 10D);

        return y + 3D;
    }

    private double paintCores(final GraphicsContext gc, final double startX, final double startY, final double width, final Cpu cpu) {
        final double coreWidth = width - getSettings().getMarginInner().getLeft() - getSettings().getMarginInner().getRight();

        double y = startY;

        for (int i = 0; i < cpu.getNumberOfCores(); i++) {
            y = paintCore(gc, startX, y, coreWidth, cpu.getCore(i));
        }

        return y - 3D;
    }

    private double paintTotal(final GraphicsContext gc, final double startX, final double startY, final double width, final Cpu cpu) {
        final double fontSize = getSettings().getFontSize();

        gc.setFont(getSettings().getFont());

        // CpuLoads
        final CpuLoadAvg cpuLoadAvg = cpu.getCpuLoadAvg();
        final double y = startY + fontSize;
        paintText(gc, String.format("Total: %.1f°C", cpu.getTemperature()), startX, y);

        paintTextAndValue(gc,
                "Loads:", String.format("%.2f %.2f %.2f", cpuLoadAvg.oneMinute(), cpuLoadAvg.fiveMinutes(), cpuLoadAvg.fifteenMinutes()),
                startX + width - (fontSize * 13D), y);

        return y;
    }

    private double paintTotalBar(final GraphicsContext gc, final double startX, final double startY, final double width, final Cpu cpu) {
        final double fontSize = getSettings().getFontSize();

        final double usage = cpu.getUsage();
        double y = startY + fontSize;
        paintText(gc, String.format("%3.0f%% ", usage * 100D), startX, y);

        final double x = startX + 40D;
        y += 3D;
        final double barWidth = width - x;

        // Horizontaler Gradient.
        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, gradientStops));
        gc.fillRect(x, y - fontSize, usage * barWidth, 10D);

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y - fontSize, barWidth, 10D);

        return y;
    }

    private double paintTotalGraph(final GraphicsContext gc, final double startX, final double startY, final double width) {
        final Values<Double> values = coreUsageMap.computeIfAbsent(-1, key -> new Values<>());
        final List<Double> valueList = values.getLastValues((int) width);
        final double height = 20D;

        // Vertikaler Gradient.
        gc.setStroke(new LinearGradient(
                startX,
                startY + height,
                startX,
                startY,
                false,
                CycleMethod.NO_CYCLE,
                gradientStops)
        );

        final double xOffset = startX + width - valueList.size(); // Diagramm von rechts aufbauen.
        // final double xOffset = startX; // Diagramm von links aufbauen.

        for (int i = 0; i < valueList.size(); i++) {
            final double value = valueList.get(i);
            final double x = i + xOffset;
            final double valueHeight = value * height;

            gc.strokeLine(x, startY + height, x, startY + height - valueHeight);
        }

        drawDebugBorder(gc, startX, startY, width, height);

        return startY + height;
    }
}
