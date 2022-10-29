// Created: 02.06.2017
package de.freese.jsensors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.WindowConstants;

import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.registry.ScheduledSensorRegistry;
import de.freese.jsensors.utils.JSensorThreadFactory;
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

/**
 * @author Thomas Freese
 */
public class JFreeChartDemo
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static void main(final String[] args) throws Exception
    {
        ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("scheduler"), 2);
        registry.start();

        new CpuMetrics().bindTo(registry);
        TimeSeries timeSeriesCpuUsage = new TimeSeries("cpu.usage");

        registry.scheduleSensor("cpu.usage", 1, 1, TimeUnit.SECONDS, sensorValue ->
        {
            if ((sensorValue.getValue() == null) || sensorValue.getValue().isBlank())
            {
                return;
            }

            RegularTimePeriod timePeriod = new FixedMillisecond(sensorValue.getTimestamp());
            timeSeriesCpuUsage.add(timePeriod, sensorValue.getValueAsDouble());
        });

        new MemoryMetrics().bindTo(registry);
        TimeSeries timeSeriesMemoryUsage = new TimeSeries("memory.usage");

        registry.scheduleSensor("memory.usage", 1, 1, TimeUnit.SECONDS, sensorValue ->
        {
            if ((sensorValue.getValue() == null) || sensorValue.getValue().isBlank())
            {
                return;
            }

            RegularTimePeriod timePeriod = new FixedMillisecond(sensorValue.getTimestamp());
            timeSeriesMemoryUsage.add(timePeriod, sensorValue.getValueAsDouble());
        });

        // Nur die letzten N Daten vorhalten.
        // timeSeriesCpuUsage.setMaximumItemCount(1500);
        // timeSeriesMemoryUsage.setMaximumItemCount(1500);

        // Nur Daten der letzten Minute vorhalten.
        timeSeriesCpuUsage.setMaximumItemAge(60 * 1000L);
        timeSeriesMemoryUsage.setMaximumItemAge(60 * 1000L);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeriesCpuUsage);
        dataset.addSeries(timeSeriesMemoryUsage);

        Font font = new Font("Arial", Font.BOLD, 12);

        ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        timeAxis.setFixedAutoRange(60 * 1000D); // 1 Minute
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        NumberAxis valueAxis = new NumberAxis("Usage [%]");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickLabelFont(font);
        valueAxis.setLabelFont(font);
        // valueAxis.setAutoRange(true);
        // valueAxis.setFixedAutoRange(10000D);
        // valueAxis.setAutoTickUnitSelection(true);
        // valueAxis.setRange(0.0D, 20000D);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.5F));

        XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

        JFreeChart chart = new JFreeChart(null, null, xyplot, true);
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(font);

        // ChartPanel chartPanel = new ChartPanel(chart);

        ChartFrame chartFrame = new ChartFrame("JSensors", chart, true);
        chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        chartFrame.setSize(1280, 800);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent event)
            {
                registry.stop();
                System.exit(0);
            }
        });
        chartFrame.setVisible(true);
    }
}
