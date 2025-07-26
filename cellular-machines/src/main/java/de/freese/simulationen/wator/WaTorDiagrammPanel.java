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

    private static ScheduledExecutorService getScheduledExecutorService() {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }
    
    private final TimeSeries timeSeriesFische;
    private final TimeSeries timeSeriesHaie;

    public WaTorDiagrammPanel() {
        super();

        timeSeriesFische = new TimeSeries("Fische");
        timeSeriesHaie = new TimeSeries("Haie");

        // Nur die letzten N Daten vorhalten.
        // timeSeriesFische.setMaximumItemCount(1500);
        // timeSeriesHaie.setMaximumItemCount(1500);

        // Nur Daten der letzten Minute vorhalten.
        timeSeriesFische.setMaximumItemAge(60L * 1000L);
        timeSeriesHaie.setMaximumItemAge(60L * 1000L);

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeriesFische);
        dataset.addSeries(timeSeriesHaie);

        final Font font = new Font("Arial", Font.BOLD, 12);

        // Domain Axis
        final ValueAxis xAxis = new DateAxis("Zeitachse");
        xAxis.setLowerMargin(0.02D);
        xAxis.setUpperMargin(0.02D);
        xAxis.setAutoRange(true);
        xAxis.setFixedAutoRange(60D * 1000D); // 1 Minute
        xAxis.setTickLabelsVisible(true);
        xAxis.setTickLabelFont(font);
        xAxis.setLabelFont(font);

        // Range Axis
        final NumberAxis yAxis = new NumberAxis("Anzahl");
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);
        // yAxis.setAutoRange(true);
        // yAxis.setFixedAutoRange(10000D);
        // yAxis.setAutoTickUnitSelection(true);
        // yAxis.setRange(0.0D, 20000D);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.5F));

        // final JFreeChart chart = ChartFactory.createXYLineChart("", "Zeitachse", "Anzahl", data, PlotOrientation.VERTICAL, true, true, false);
        // final XYPlot plot = (XYPlot) chart.getPlot();

        final XYPlot xyplot = new XYPlot(dataset, xAxis, yAxis, renderer);

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

            timeSeriesFische.addOrUpdate(timePeriod, fishes);
            timeSeriesHaie.addOrUpdate(timePeriod, sharks);

            // Synchronising the painting on systems that buffer graphics events.
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
}
