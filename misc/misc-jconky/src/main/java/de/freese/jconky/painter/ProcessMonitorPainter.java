// Created: 10.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.util.JConkyUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class ProcessMonitorPainter extends AbstractMonitorPainter
{
    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        ProcessInfos infos = getContext().getProcessInfos();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize + 0D;
        paintTitle(gc, "Processes", x, y, width);

        // Alive
        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 1.25D;
        paintTextAndValue(gc, "Alive:", Integer.toString(infos.size()), x, y);

        // Running
        x += 100D;
        paintTextAndValue(gc, "Running:", Integer.toString(infos.getRunning()), x, y);

        // Uptime
        x += 105D;
        paintTextAndValue(gc, "Uptime:", JConkyUtils.toClockString(getContext().getUptimeInSeconds()), x, y);

        // Highest Usage
        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 2D;

        paintText(gc, "Highest Usage", x, y);
        paintText(gc, String.format("%8s %8s %8s", "PID", "CPU%", "MEM%"), x + 100D, y);

        for (ProcessInfo processInfo : infos.getSortedByCpuUsage(3))
        {
            y += fontSize * 1.25D;

            paintText(gc, processInfo.getName(), x, y);
            paintText(gc, String.format("%8d %8.2f %8.2f", processInfo.getPid(), processInfo.getCpuUsage() * 100D, processInfo.getMemoryUsage() * 100D),
                    x + 100D, y);
        }

        // Highest Memory
        y += fontSize * 2D;

        paintText(gc, "Highest Memory", x, y);
        paintText(gc, String.format("%8s %8s %8s", "PID", "MEM%", "CPU%"), x + 100D, y);

        for (ProcessInfo processInfo : infos.getSortedByMemoryUsage(3))
        {
            y += fontSize * 1.25D;

            paintText(gc, processInfo.getName(), x, y);
            paintText(gc, String.format("%8d %8.2f %8.2f", processInfo.getPid(), processInfo.getMemoryUsage() * 100D, processInfo.getCpuUsage() * 100D),
                    x + 100D, y);
        }

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
