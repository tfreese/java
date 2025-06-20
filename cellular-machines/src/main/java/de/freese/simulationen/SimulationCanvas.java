// Created: 18.09.2009
package de.freese.simulationen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import de.freese.simulationen.model.Simulation;
import de.freese.simulationen.model.SimulationListener;

/**
 * Zeichenfläche für die Simulationen.
 *
 * @author Thomas Freese
 */
public class SimulationCanvas extends JComponent implements SimulationListener {
    @Serial
    private static final long serialVersionUID = 4896850562260701814L;

    public static BufferedImage copyImage(final BufferedImage source) {
        final BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        final Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        return b;
    }

    private final boolean useVolatileImage;

    private transient Image image;
    private transient VolatileImage volatileImage;

    public SimulationCanvas(final Simulation simulation) {
        // Die Größe der Simulation auf die Anzeigegröße skalieren.
        this(simulation, (int) (800 * (double) simulation.getWidth()) / simulation.getHeight(), 800);
    }

    public SimulationCanvas(final Simulation simulation, final int width, final int height) {
        super();

        setPreferredSize(new Dimension(width, height));

        useVolatileImage = SimulationEnvironment.getInstance().getAsBoolean("simulation.use.volatileImage", false);

        completed(simulation);

        simulation.addWorldListener(this);
        // setDoubleBuffered(true);
    }

    @Override
    public void completed(final Simulation simulation) {
        image = simulation.getImage();

        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    public void paint(final Graphics g) {
        final int x = 0;
        final int y = 0;

        if (!useVolatileImage) {
            g.drawImage(image, x, y, getWidth(), getHeight(), null);

            return;
        }

        // Main rendering loop. Volatile images may lose their contents.
        // This loop will continually render to (and produce if necessary) volatile images
        // until the rendering was completed successfully.
        if (volatileImage == null) {
            volatileImage = createVolatileImage();
        }

        do {
            // Validate the volatile image for the graphics configuration of this
            // component. If the volatile image doesn't apply for this graphics configuration
            // (in other words, the hardware acceleration doesn't apply for the new device)
            // then we need to re-create it.
            final GraphicsConfiguration gc = getGraphicsConfiguration();

            // This means the device doesn't match up to this hardware accelerated image.
            if (volatileImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
                volatileImage = null;
                // createBackBuffer(); // recreate the hardware accelerated image.

                g.drawImage(image, x, y, getWidth(), getHeight(), null);

                return;
            }

            final Graphics offscreenGraphics = volatileImage.getGraphics();
            offscreenGraphics.drawImage(image, x, y, getWidth(), getHeight(), null);

            // paint back buffer to main graphics
            g.drawImage(volatileImage, x, y, getWidth(), getHeight(), this);
            // g.dispose();
        }
        while (volatileImage.contentsLost()); // Test if content is lost

        // g.dispose(); // Dispose nur wenn man es selbst erzeugt hat.
    }

    @Override
    protected void paintChildren(final Graphics g) {
        // There are no Children.
        // super.paintChildren(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        // Ignore
        // super.paintComponent(g);
    }

    /**
     * BackBuffer, erzeugt lazy das {@link VolatileImage} wenn nötig.
     */
    private VolatileImage createVolatileImage() {
        // GraphicsConfiguration gc = getGraphicsConfiguration();
        // return gc.createCompatibleVolatileImage(getWidth(), getHeight());

        return createVolatileImage(getPreferredSize().width, getPreferredSize().height);
    }
}
