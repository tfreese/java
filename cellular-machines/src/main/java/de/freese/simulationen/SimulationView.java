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
    private Duration delay = Duration.ofMillis(40);
    private JPanel mainPanel;
    private ScheduledFuture<?> scheduledFuture;
    private S simulation;

    public S getSimulation() {
        return this.simulation;
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
        if (this.controlPanel == null) {
            this.controlPanel = new JPanel();
        }

        return this.controlPanel;
    }

    /**
     * Liefert das Delay für das ScheduledFuture zum Berechnen der Simulationen.
     */
    protected Duration getDelay() {
        return this.delay;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
            this.mainPanel.setDoubleBuffered(true);
        }

        return this.mainPanel;
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

        this.scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(runnable, 0, getDelay().toMillis(), TimeUnit.MILLISECONDS);

        this.buttonStart.setEnabled(false);
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
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }

        this.buttonStart.setEnabled(true);
    }

    private JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new BorderLayout());

            this.buttonStart = new JButton("Start");
            this.buttonStart.addActionListener(event -> start());
            this.buttonPanel.add(this.buttonStart, BorderLayout.WEST);

            final JButton buttonStop = new JButton("Stop");
            buttonStop.addActionListener(event -> stop());
            this.buttonPanel.add(buttonStop, BorderLayout.EAST);

            final JButton buttonStep = new JButton("Step");
            buttonStep.addActionListener(event -> step());
            this.buttonPanel.add(buttonStep, BorderLayout.NORTH);

            final JButton buttonReset = new JButton("Reset");
            buttonReset.addActionListener(event -> reset());
            this.buttonPanel.add(buttonReset, BorderLayout.SOUTH);
        }

        return this.buttonPanel;
    }
}
