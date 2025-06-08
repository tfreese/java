// Created: 18.09.2009
package de.freese.simulationen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.simulationen.model.Simulation;

/**
 * BasisView für die Simulationen.
 *
 * @param <S> Konkreter Typ der Welt
 *
 * @author Thomas Freese
 */
public class SimulationView<S extends Simulation> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JPanel buttonPanel;
    private JButton buttonStart;
    private JPanel controlPanel;
    /**
     * 40 ms = 25 Bilder/Sekunde
     */
    private Duration delay = Duration.ofMillis(40L);
    private JPanel mainPanel;
    private ScheduledFuture<?> scheduledFuture;
    private S simulation;

    public S getSimulation() {
        return simulation;
    }

    public void initialize(final S simulation, final Duration delay) {
        this.simulation = simulation;
        this.delay = delay;

        getControlPanel().setLayout(new BorderLayout());
        getControlPanel().setPreferredSize(new Dimension(180, 10));
        getControlPanel().add(getButtonPanel(), BorderLayout.NORTH);

        getMainPanel().setLayout(new BorderLayout());
        getMainPanel().add(getControlPanel(), BorderLayout.EAST);

        final SimulationCanvas canvas = new SimulationCanvas(simulation);
        getMainPanel().add(canvas, BorderLayout.CENTER);
    }

    protected JPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new JPanel();
        }

        return controlPanel;
    }

    /**
     * Liefert das Delay für das ScheduledFuture zum Berechnen der Simulationen.
     */
    protected Duration getDelay() {
        return delay;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setDoubleBuffered(true);
        }

        return mainPanel;
    }

    protected ScheduledExecutorService getScheduledExecutorService() {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }

    protected void reset() {
        stop();
        getSimulation().reset();
        start();
    }

    protected void start() {
        final Runnable runnable = this::step;

        scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(runnable, 0, getDelay().toMillis(), TimeUnit.MILLISECONDS);

        buttonStart.setEnabled(false);
    }

    protected void step() {
        try {
            // long start = System.currentTimeMillis();
            getSimulation().nextGeneration();
            // System.out.printf("%d ms%n", System.currentTimeMillis() - start);
        }
        catch (Exception ex) {
            stop();

            getLogger().error(ex.getMessage(), ex);

            final StringWriter sw = new StringWriter();

            try (PrintWriter pw = new PrintWriter(sw)) {
                ex.printStackTrace(pw);
            }

            JOptionPane.showMessageDialog(getMainPanel(), sw);
        }
    }

    protected void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }

        buttonStart.setEnabled(true);
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());

            buttonStart = new JButton("Start");
            buttonStart.addActionListener(event -> start());
            buttonPanel.add(buttonStart, BorderLayout.WEST);

            final JButton buttonStop = new JButton("Stop");
            buttonStop.addActionListener(event -> stop());
            buttonPanel.add(buttonStop, BorderLayout.EAST);

            final JButton buttonStep = new JButton("Step");
            buttonStep.addActionListener(event -> step());
            buttonPanel.add(buttonStep, BorderLayout.NORTH);

            final JButton buttonReset = new JButton("Reset");
            buttonReset.addActionListener(event -> reset());
            buttonPanel.add(buttonReset, BorderLayout.SOUTH);
        }

        return buttonPanel;
    }
}
