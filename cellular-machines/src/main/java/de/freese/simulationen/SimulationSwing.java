// Created: 18.09.2009
package de.freese.simulationen;

import java.awt.GraphicsEnvironment;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import de.freese.simulationen.ant.AntRasterSimulation;
import de.freese.simulationen.balls.BallSimulation;
import de.freese.simulationen.balls.BallView;
import de.freese.simulationen.factal.FractalRasterSimulation;
import de.freese.simulationen.gameoflife.GoFRasterSimulation;
import de.freese.simulationen.wator.WaTorDiagrammPanel;
import de.freese.simulationen.wator.WaTorRasterSimulation;
import de.freese.simulationen.wator.WaTorView;

/**
 * Hauptfenster der Simulation-Demos.
 *
 * @author Thomas Freese
 */
class SimulationSwing extends JFrame
{
    @Serial
    private static final long serialVersionUID = -8931412063622174282L;

    SimulationSwing()
    {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    }

    public void initialize()
    {
        setTitle("Simulationen");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        int delay = SimulationEnvironment.getInstance().getAsInt("simulation.delay", 40);
        int fieldWidth = SimulationEnvironment.getInstance().getAsInt("simulation.field.width", 100);
        int fieldHeight = SimulationEnvironment.getInstance().getAsInt("simulation.field.height", 100);

        // Ants: Die Ameisen mögen es etwas schneller.
        SimulationView<AntRasterSimulation> ant2 = new SimulationView<>();
        ant2.initialize(new AntRasterSimulation(fieldWidth, fieldHeight), Math.max(delay / 5, 1));
        tabbedPane.addTab("Langton-Ameise", ant2.getMainPanel());

        // GoF: Game of Life
        SimulationView<GoFRasterSimulation> gofView2 = new SimulationView<>();
        gofView2.initialize(new GoFRasterSimulation(fieldWidth, fieldHeight), delay);
        tabbedPane.addTab("Game of Life", gofView2.getMainPanel());

        // Bälle
        BallView ballView = new BallView();
        ballView.initialize(new BallSimulation(fieldWidth, fieldHeight, delay * 4), delay);
        tabbedPane.addTab("Bälle", ballView.getMainPanel());

        // Fraktale
        SimulationView<FractalRasterSimulation> fractalView = new SimulationView<>();
        fractalView.initialize(new FractalRasterSimulation(fieldWidth, fieldHeight), delay);
        tabbedPane.addTab("Fraktale", fractalView.getMainPanel());

        // WaTor: Water Torus
        WaTorView waTorView = new WaTorView();
        waTorView.initialize(new WaTorRasterSimulation(fieldWidth, fieldHeight), delay);
        tabbedPane.addTab("Water Torus", waTorView.getMainPanel());

        WaTorDiagrammPanel waTorDiagrammPanel = new WaTorDiagrammPanel();
        waTorView.getSimulation().addWorldListener(waTorDiagrammPanel);
        tabbedPane.addTab("WaTor-Diagramm", waTorDiagrammPanel);

        // HopAlong
        // SimulationView<HopAlongRasterSimulation> hopAlongView = new SimulationView<>();
        // hopAlongView.initialize(new HopAlongRasterSimulation(fieldWidth, fieldHeight), delay);
        // tabbedPane.addTab("Hop along", hopAlongView.getMainPanel());
    }
}
