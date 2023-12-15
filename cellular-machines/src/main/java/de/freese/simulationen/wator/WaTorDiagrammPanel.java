// Created: 09.10.2009
package de.freese.simulationen.wator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.Serial;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.freese.simulationen.SimulationEnvironment;
import de.freese.simulationen.model.Simulation;
import de.freese.simulationen.model.SimulationListener;

/**
 * DiagrammPanel der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class WaTorDiagrammPanel extends JPanel implements SimulationListener {
    @Serial
    private static final long serialVersionUID = -7891438395009637657L;

    private final TimeSeries timeSeriesFische;
    private final TimeSeries timeSeriesHaie;

    public WaTorDiagrammPanel() {
        super();

        this.timeSeriesFische = new TimeSeries("Fische");
        this.timeSeriesHaie = new TimeSeries("Haie");

        // Nur die letzten N Daten vorhalten.
        // this.timeSeriesFische.setMaximumItemCount(1500);
        // this.timeSeriesHaie.setMaximumItemCount(1500);

        // Nur Daten der letzten Minute vorhalten.
        this.timeSeriesFische.setMaximumItemAge(60 * 1000L);
        this.timeSeriesHaie.setMaximumItemAge(60 * 1000L);

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(this.timeSeriesFische);
        dataset.addSeries(this.timeSeriesHaie);

        final Font font = new Font("Arial", Font.BOLD, 12);

        final ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        timeAxis.setFixedAutoRange(60 * 1000D); // 1 Minute
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        final NumberAxis valueAxis = new NumberAxis("Anzahl");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickLabelFont(font);
        valueAxis.setLabelFont(font);
        // valueAxis.setAutoRange(true);
        // valueAxis.setFixedAutoRange(10000D);
        // valueAxis.setAutoTickUnitSelection(true);
        // valueAxis.setRange(0.0D, 20000D);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.5F));

        final XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

        final JFreeChart chart = new JFreeChart(null, null, xyplot, true);
        final LegendTitle legend = chart.getLegend();
        legend.setItemFont(font);

        setLayout(new BorderLayout());
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }

    @Override
    public void completed(final Simulation simulation) {
        final Runnable runnable = () -> {
            final WaTorRasterSimulation watorRasterSimulation = (WaTorRasterSimulation) simulation;

            final int[] fischeUndHaie = watorRasterSimulation.countFishesAndSharks();

            update(fischeUndHaie[0], fischeUndHaie[1]);
        };

        getScheduledExecutorService().execute(runnable);
    }

    /**
     * Aktualisiert das Diagramm.
     */
    protected void update(final int fishes, final int sharks) {
        final Runnable runnable = () -> {
            final RegularTimePeriod timePeriod = new FixedMillisecond();

            this.timeSeriesFische.addOrUpdate(timePeriod, fishes);
            this.timeSeriesHaie.addOrUpdate(timePeriod, sharks);

            // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
            // Without this line, the animation might not be smooth on Linux.
            Toolkit.getDefaultToolkit().sync();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }
}
