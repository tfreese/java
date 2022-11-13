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

import de.freese.simulationen.model.SimulationType;

/**
 * -swing<br>
 * -console -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen<br>
 *
 * @author Thomas Freese
 */
public final class SimulationLauncher
{
    public static void main(final String[] args) throws Exception
    {
        SimulationEnvironment.getInstance().init();

        if (args.length == 0)
        {
            throw new IllegalArgumentException("parameter required: -swing or -console");
        }

        List<String> parameter = new ArrayList<>(List.of(args));

        if ("-swing".equals(parameter.get(0)))
        {
            launchSwing();

            return;
        }
        else if ("-console".equals(parameter.get(0)))
        {
            parameter.remove(0);

            launchConsole(parameter);

            return;
        }

        throw new IllegalArgumentException("parameter not supported");
    }

    /**
     * -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen
     */
    private static void launchConsole(final List<String> parameter)
    {
        SimulationType type = null;
        int cycles = 0;
        int width = 0;
        int height = 0;
        Path path = null;

        while (!parameter.isEmpty())
        {
            if ("-type".equals(parameter.get(0)))
            {
                parameter.remove(0);

                type = SimulationType.findByNameShort(parameter.remove(0));
            }
            else if ("-cycles".equals(parameter.get(0)))
            {
                parameter.remove(0);

                cycles = Integer.parseInt(parameter.remove(0));
            }
            else if ("-size".equals(parameter.get(0)))
            {
                parameter.remove(0);

                width = Integer.parseInt(parameter.remove(0));
                height = Integer.parseInt(parameter.remove(0));
            }
            else if ("-dir".equals(parameter.get(0)))
            {
                parameter.remove(0);

                path = Paths.get(parameter.remove(0));
            }
        }

        SimulationConsole simulationConsole = new SimulationConsole();
        simulationConsole.start(type, cycles, width, height, path);

        SimulationEnvironment.getInstance().shutdown();
    }

    private static void launchSwing()
    {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
        {
            if (throwable != null)
            {
                throwable.printStackTrace();
            }

            System.exit(-1);
        });

        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Single-Monitor
        // int width = (int) screenSize.getWidth() - 75;
        // int height = (int) screenSize.getHeight() - 75;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); // Multi-Monitor
        // GraphicsDevice gd = ge.getDefaultScreenDevice(); // Haupt-Monitor
        GraphicsDevice[] gds = ge.getScreenDevices();

        int maxWidth = 0;
        int maxHeight = 0;

        for (GraphicsDevice gd : gds)
        {
            Rectangle r = gd.getDefaultConfiguration().getBounds();
            maxWidth = Math.max(maxWidth, (int) r.getWidth());
            maxHeight = Math.max(maxHeight, (int) r.getHeight());

            // DisplayMode displayMode = gd.getDisplayMode();
            // maxWidth = Math.max(maxWidth, displayMode.getWidth());
            // maxHeight = Math.max(maxHeight, displayMode.getHeight());
        }

        int width = maxWidth - 75;
        int height = maxHeight - 75;

        SimulationSwing demo = new SimulationSwing();
        demo.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent event)
            {
                SimulationEnvironment.getInstance().shutdown();
                System.exit(0);
            }
        });

        SwingUtilities.invokeLater(() ->
        {
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

    private SimulationLauncher()
    {
        super();
    }
}
