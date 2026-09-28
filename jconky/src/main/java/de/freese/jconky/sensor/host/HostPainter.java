package de.freese.jconky.sensor.host;

import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;

import de.freese.jconky.sensor.AbstractSensorPainter;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public final class HostPainter extends AbstractSensorPainter {
    private final HostSensor hostSensor;

    public HostPainter(final HostSensor hostSensor) {
        super();

        this.hostSensor = Objects.requireNonNull(hostSensor, "hostSensor required");
    }

    @Override
    public double repaint(final GraphicsContext gc, final double startX, final double startY, final double width) {
        final Host host = hostSensor.getHost();

        gc.setFont(getSettings().getFont());

        final double fontSize = getSettings().getFontSize();

        final double y = startY + fontSize;

        paintText(gc, String.format("%s - %s on %s", host.name(), host.version(), host.architecture()), startX, y);

        drawDebugBorder(gc, startX, startY, width, y);

        return y;
    }
}
