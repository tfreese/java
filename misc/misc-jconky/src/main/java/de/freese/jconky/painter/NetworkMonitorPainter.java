// Created: 10.12.2020
package de.freese.jconky.painter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.freese.jconky.model.NetworkInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.NetworkProtocolInfo;
import de.freese.jconky.model.Values;
import de.freese.jconky.util.JConkyUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class NetworkMonitorPainter extends AbstractMonitorPainter
{
    private final Map<String, Values<Double>> downloadMap = new HashMap<>();

    private final Stop[] gradientStops;

    private final Map<String, Values<Double>> uploadMap = new HashMap<>();

    public NetworkMonitorPainter()
    {
        super();

        this.gradientStops = new Stop[]
                {
                        new Stop(0D, Color.WHITE), new Stop(1D, getSettings().getColorValue())
                };
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        NetworkInfos networkInfos = getContext().getNetworkInfos();
        NetworkInfo lan = networkInfos.getByName("enp5s0");

        this.downloadMap.computeIfAbsent(lan.getInterfaceName(), key -> new Values<>()).addValue(lan.getDownloadPerSecond());
        this.uploadMap.computeIfAbsent(lan.getInterfaceName(), key -> new Values<>()).addValue(lan.getUploadPerSecond());

        NetworkProtocolInfo protocolInfo = networkInfos.getProtocolInfo();
        String externalIp = getContext().getExternalIp();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, String.format("Network: %s -> %s", lan.getIp(), externalIp), x, y, width);

        y += fontSize * 1.25D;

        gc.save();
        gc.translate(x, y);
        y += paintInterface(gc, width - x - getSettings().getMarginInner().getRight(), lan);
        gc.restore();

        y += fontSize * 1.25D;
        paintTextAndValue(gc, "TCP-Connections:", Integer.toString(protocolInfo.getTcpConnections()), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    private double paintInterface(final GraphicsContext gc, final double width, final NetworkInfo networkInfo)
    {
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;
        paintTextAndValue(gc, "Download:", JConkyUtils.toHumanReadableSize(networkInfo.getDownloadPerSecond(), "%.0f %s"), x, y);

        x = width - (fontSize * 10.5D);
        paintTextAndValue(gc, "Upload:", JConkyUtils.toHumanReadableSize(networkInfo.getUploadPerSecond(), "%.0f %s"), x, y);

        int graphWidth = (int) (width / 2) - 10;
        int graphHeight = 40;

        x = 0D;
        y += fontSize - 4D;

        gc.save();
        gc.translate(x, y);
        paintInterfaceGraph(gc, graphWidth, graphHeight, this.downloadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));
        gc.restore();

        gc.save();
        gc.translate(x + graphWidth + 20D, y);
        paintInterfaceGraph(gc, graphWidth, graphHeight, this.uploadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));
        gc.restore();

        y += graphHeight + fontSize + 3;
        paintTextAndValue(gc, String.format("%s: Total:", networkInfo.getInterfaceName()), JConkyUtils.toHumanReadableSize(networkInfo.getBytesReceived()), x,
                y);

        x = width - (fontSize * 10.5D);
        paintTextAndValue(gc, "Total:", JConkyUtils.toHumanReadableSize(networkInfo.getBytesTransmitted()), x, y);

        return y;
    }

    private void paintInterfaceGraph(final GraphicsContext gc, final double width, final double height, final Values<Double> values)
    {
        List<Double> valueList = values.getLastValues((int) width);

        double minValue = 0D;
        // double maxValue = 28D * 1024D * 1024D; // 28 MB/s als max. bei 200er Leitung.
        double maxValue = values.getMaxValue();
        double minNorm = 0D;
        double maxNorm = height - 2;

        // gc.setFill(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.setStroke(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));

        double xOffset = width - valueList.size(); // Diagramm von rechts aufbauen.
        // double xOffset = 0D; // Diagramm von links aufbauen.

        for (int i = 0; i < valueList.size(); i++)
        {
            double value = valueList.get(i);
            double x = i + xOffset;
            double y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

            // gc.fillRect(x, height - 1 - y, 1, y);
            gc.strokeLine(x, height - 1 - y, x, height - 1);
        }

        // drawDebugBorder(gc, width, height);
    }
}
