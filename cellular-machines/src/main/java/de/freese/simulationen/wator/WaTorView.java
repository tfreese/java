// Created: 18.09.2009
package de.freese.simulationen.wator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.freese.simulationen.SimulationView;

/**
 * View für die WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class WaTorView extends SimulationView<WaTorRasterSimulation>
{
    /**
     * @see de.freese.simulationen.SimulationView#initialize(de.freese.simulationen.model.Simulation, int)
     */
    @Override
    public void initialize(final WaTorRasterSimulation simulation, final int delay)
    {
        super.initialize(simulation, delay);

        // Slider für Settings
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridLayout(3, 1));

        // Startenergie
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Startenergie -> Reset"));

        JSlider slider = createSlider("Fische", getSimulation().getFishStartEnergy(), Color.GREEN);
        slider.addChangeListener(event ->
        {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setFishStartEnergy(value);
            }
        });
        panel.add(slider);

        slider = createSlider("Haie", getSimulation().getSharkStartEnergy(), Color.BLUE);
        slider.addChangeListener(event ->
        {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkStartEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        // Brutenergie
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Brutenergie"));

        slider = createSlider("Fische", getSimulation().getFishBreedEnergy(), Color.GREEN);
        slider.addChangeListener(event ->
        {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setFishBreedEnergy(value);
            }
        });
        panel.add(slider);

        slider = createSlider("Haie", getSimulation().getSharkBreedEnergy(), Color.BLUE);
        slider.addChangeListener(event ->
        {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkBreedEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        // Sterbeenergie
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Sterbeenergie"));

        panel.add(Box.createGlue());

        slider = createSlider("Haie", getSimulation().getSharkStarveEnergy(), Color.BLUE);
        slider.addChangeListener(event ->
        {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkStarveEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        getControlPanel().add(sliderPanel, BorderLayout.CENTER);
    }

    /**
     * @param title String
     * @param value int
     * @param titleColor {@link Color}
     *
     * @return {@link JSlider}
     */
    private JSlider createSlider(final String title, final int value, final Color titleColor)
    {
        JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 20, value);
        TitledBorder border = new TitledBorder(title);
        border.setTitleColor(titleColor);
        slider.setBorder(border);
        slider.setPaintLabels(true);
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        // slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);

        return slider;
    }
}
