// Created: 04.03.2021
package de.freese.simulationen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.simulationen.model.SimulationType;

/**
 * -swing<br>
 * -console -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen<br>
 *
 * @author Thomas Freese
 */
public final class SimulationLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationLauncher.class);

    public static void main(final String[] args) {
        try {
            SimulationEnvironment.getInstance().init();

            if (args.length == 0) {
                throw new IllegalArgumentException("parameter required: -swing or -console");
            }

            final List<String> parameter = new ArrayList<>(List.of(args));

            if ("-swing".equals(parameter.getFirst())) {
                launchSwing();

                return;
            }
            else if ("-console".equals(parameter.getFirst())) {
                parameter.removeFirst();

                launchConsole(parameter);

                return;
            }

            throw new IllegalArgumentException("parameter not supported");
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen
     */
    private static void launchConsole(final List<String> parameter) {
        SimulationType type = null;
        int cycles = 0;
        int width = 0;
        int height = 0;
        Path path = null;

        while (!parameter.isEmpty()) {
            if ("-type".equals(parameter.getFirst())) {
                parameter.removeFirst();

                type = SimulationType.findByNameShort(parameter.removeFirst());
            }
            else if ("-cycles".equals(parameter.getFirst())) {
                parameter.removeFirst();

                cycles = Integer.parseInt(parameter.removeFirst());
            }
            else if ("-size".equals(parameter.getFirst())) {
                parameter.removeFirst();

                width = Integer.parseInt(parameter.removeFirst());
                height = Integer.parseInt(parameter.removeFirst());
            }
            else if ("-dir".equals(parameter.getFirst())) {
                parameter.removeFirst();

                path = Paths.get(parameter.removeFirst());
            }
        }

        final SimulationConsole simulationConsole = new SimulationConsole();
        simulationConsole.start(type, cycles, width, height, path);

        SimulationEnvironment.getInstance().shutdown();
    }

    private static void launchSwing() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable != null) {
                LOGGER.error(throwable.getMessage(), throwable);
            }

            System.exit(-1);
        });

        // final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Single-Monitor
        // int width = (int) screenSize.getWidth() - 75;
        // int height = (int) screenSize.getHeight() - 75;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); // Multi-Monitor
        // final GraphicsDevice gd = ge.getDefaultScreenDevice(); // Haupt-Monitor
        final GraphicsDevice[] gds = ge.getScreenDevices();

        int maxWidth = 0;
        int maxHeight = 0;

        for (GraphicsDevice gd : gds) {
            final Rectangle r = gd.getDefaultConfiguration().getBounds();
            maxWidth = Math.max(maxWidth, (int) r.getWidth());
            maxHeight = Math.max(maxHeight, (int) r.getHeight());

            // final DisplayMode displayMode = gd.getDisplayMode();
            // maxWidth = Math.max(maxWidth, displayMode.getWidth());
            // maxHeight = Math.max(maxHeight, displayMode.getHeight());
        }

        final int width = maxWidth - 75;
        final int height = maxHeight - 75;

        final SimulationSwing demo = new SimulationSwing();
        demo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                SimulationEnvironment.getInstance().shutdown();
                System.exit(0);
            }
        });

        SwingUtilities.invokeLater(() -> {
            demo.setSize(width, height);
            // demo.setPreferredSize(new Dimension(width, height));
            demo.setResizable(true);
            // demo.pack();
            demo.initialize();
            demo.setLocationRelativeTo(null);
            // demo.setExtendedState(Frame.MAXIMIZED_BOTH); // Full-Screen
            demo.setVisible(true);
        });
    }

    private SimulationLauncher() {
        super();
    }
}
