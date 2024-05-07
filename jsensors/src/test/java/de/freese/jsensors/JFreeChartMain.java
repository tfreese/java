// Created: 02.06.2017
package de.freese.jsensors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.WindowConstants;

import org.jfree.chart.ChartFrame;
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

import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.registry.ScheduledSensorRegistry;
import de.freese.jsensors.utils.JSensorThreadFactory;

/**
 * @author Thomas Freese
 */
public final class JFreeChartMain {
    public static void main(final String[] args) throws Exception {
        final ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("scheduler-%d"), 2);
        registry.start();

        final TimeSeries timeSeriesCpuUsage = new TimeSeries("cpu.usage");
        new CpuMetrics().bindTo(registry, name -> sensorValue -> {
            if (sensorValue.getValue() == null || sensorValue.getValue().isBlank()) {
                return;
            }

            final RegularTimePeriod timePeriod = new FixedMillisecond(sensorValue.getTimestamp());
            timeSeriesCpuUsage.add(timePeriod, sensorValue.getValueAsDouble());
        });
        registry.scheduleSensor("cpu.usage", 1, 1, TimeUnit.SECONDS);

        final TimeSeries timeSeriesMemoryUsage = new TimeSeries("memory.usage");
        new MemoryMetrics().bindTo(registry, name -> sensorValue -> {
            if (sensorValue.getValue() == null || sensorValue.getValue().isBlank()) {
                return;
            }

            final RegularTimePeriod timePeriod = new FixedMillisecond(sensorValue.getTimestamp());
            timeSeriesMemoryUsage.add(timePeriod, sensorValue.getValueAsDouble());
        });
        registry.scheduleSensor("memory.usage", 1, 1, TimeUnit.SECONDS);

        // Nur die letzten N Daten vorhalten.
        // timeSeriesCpuUsage.setMaximumItemCount(1500);
        // timeSeriesMemoryUsage.setMaximumItemCount(1500);

        // Nur Daten der letzten Minute vorhalten.
        timeSeriesCpuUsage.setMaximumItemAge(60 * 1000L);
        timeSeriesMemoryUsage.setMaximumItemAge(60 * 1000L);

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeriesCpuUsage);
        dataset.addSeries(timeSeriesMemoryUsage);

        final Font font = new Font("Arial", Font.BOLD, 12);

        final ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        timeAxis.setFixedAutoRange(60 * 1000D); // 1 Minute
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        final NumberAxis valueAxis = new NumberAxis("Usage [%]");
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

        // final ChartPanel chartPanel = new ChartPanel(chart);

        final ChartFrame chartFrame = new ChartFrame("JSensors", chart, true);
        chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        chartFrame.setSize(1280, 800);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                registry.stop();
                System.exit(0);
            }
        });
        chartFrame.setVisible(true);
    }

    private JFreeChartMain() {
        super();
    }
}
