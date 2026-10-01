package de.freese.jconky.sensor.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import de.freese.jconky.sensor.AbstractSensorPainter;
import de.freese.jconky.sensor.Values;
import de.freese.jconky.util.JConkyUtils;

/**
 * @author Thomas Freese
 * @since 01.10.26
 */
public final class NetworkPainter extends AbstractSensorPainter {
    private final Map<String, Values<Double>> downloadMap = new HashMap<>();
    private final ExternalIpSensor externalIpSensor;
    private final Stop[] gradientStops;
    private final NetworkSensor networkSensor;
    private final Map<String, Values<Double>> uploadMap = new HashMap<>();

    public NetworkPainter(final NetworkSensor networkSensor, final ExternalIpSensor externalIpSensor) {
        super();

        this.networkSensor = Objects.requireNonNull(networkSensor, "networkSensor required");
        this.externalIpSensor = Objects.requireNonNull(externalIpSensor, "externalIpSensor required");
        gradientStops = new Stop[]{new Stop(0D, Color.WHITE), new Stop(1D, getSettings().getColorValue())};
    }

    @Override
    public double repaint(final GraphicsContext gc, final double startX, final double startY, final double width) {
        final NetworkInfos networkInfos = networkSensor.getNetworkInfos();

        final NetworkInfo lan = networkInfos.getByName("enp6s0");
        downloadMap.computeIfAbsent(lan.getInterfaceName(), key -> new Values<>()).addValue(lan.getDownloadPerSecond());
        uploadMap.computeIfAbsent(lan.getInterfaceName(), key -> new Values<>()).addValue(lan.getUploadPerSecond());

        gc.setFont(getSettings().getFont());

        final double fontSize = getSettings().getFontSize();

        double y = startY + fontSize;
        paintTitle(gc, String.format("Network: %s -> %s", lan.getIp(), externalIpSensor.getExternalIp()), startX, y, width);

        y += fontSize * 1.25D;

        // gc.save();
        // gc.translate(startX, y);
        y = paintInterface(gc, startX, y, width - startX - getSettings().getMarginInner().getRight(), lan);
        // gc.restore();

        // y += fontSize * 1.25D;
        // paintTextAndValue(gc, "TCP-Connections:", Integer.toString(protocolInfo.getTcpConnections()), startX, y);

        y += 5D;
        drawDebugBorder(gc, startX, startY, width, y);

        return y;
    }

    private double paintInterface(final GraphicsContext gc, final double startX, final double startY, final double width, final NetworkInfo networkInfo) {
        final double fontSize = getSettings().getFontSize();

        double x = startX;
        double y = startY;
        paintTextAndValue(gc, "Download:", JConkyUtils.toHumanReadableSize(networkInfo.getDownloadPerSecond(), "%.0f %s"), x, y);

        x = width - (fontSize * 10.5D);
        paintTextAndValue(gc, "Upload:", JConkyUtils.toHumanReadableSize(networkInfo.getUploadPerSecond(), "%.0f %s"), x, y);

        final int graphWidth = (int) (width / 2) - 10;
        final int graphHeight = 40;

        x = startX;
        y += fontSize - 4D;

        final double graphY = y;

        paintInterfaceGraph(gc, startX, graphY, graphWidth, graphHeight, downloadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));

        y = paintInterfaceGraph(gc, startX + graphWidth + 20D, graphY, graphWidth, graphHeight, uploadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));

        y += fontSize + 3D;
        paintTextAndValue(gc, String.format("%s: Total:", networkInfo.getInterfaceName()), JConkyUtils.toHumanReadableSize(networkInfo.getBytesReceived()), x, y);

        x = width - (fontSize * 7D);
        paintTextAndValue(gc, "Total:", JConkyUtils.toHumanReadableSize(networkInfo.getBytesTransmitted()), x, y);

        return y;
    }

    private double paintInterfaceGraph(final GraphicsContext gc, final double startX, final double startY, final double width, final double height, final Values<Double> values) {
        final List<Double> valueList = values.getLastValues((int) width);

        final double minValue = 0D;
        // final double maxValue = 28D * 1024D * 1024D; // 28 MB/s als max. bei 200er-Leitung.
        final double maxValue = values.getMaxValue();
        final double minNorm = 0D;
        final double maxNorm = height - 2D;

        // Vertikaler Gradient.
        gc.setStroke(new LinearGradient(
                startX,
                startY + height,
                startX,
                startY,
                false,
                CycleMethod.NO_CYCLE,
                gradientStops));

        final double xOffset = startX + width - valueList.size(); // Diagramm von rechts aufbauen.
        // double xOffset = startX; // Diagramm von links aufbauen.

        for (int i = 0; i < valueList.size(); i++) {
            final double value = valueList.get(i);
            final double x = i + xOffset;
            final double valueHeight = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

            gc.strokeLine(x, startY + height, x, startY + height - valueHeight);
        }

        drawDebugBorder(gc, startX, startY, width, height);

        return startY + height;
    }
}
