// Created: 30 Nov. 2024
package de.freese.simulationen.noise;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * @author Thomas Freese
 */
public final class WhiteNoiseDemo {
    static void main() {
        final WhiteNoiseCanvas whiteNoiseCanvas = new WhiteNoiseCanvas(100, 100);
        final WhiteNoiseComponent whiteNoiseComponent = new WhiteNoiseComponent(100, 100);

        final JPanel panelCanvas = new JPanel(new BorderLayout());
        panelCanvas.setBorder(new TitledBorder("CanvasPaint"));
        panelCanvas.setPreferredSize(new Dimension(500, 500));
        panelCanvas.add(whiteNoiseCanvas.getCanvas(), BorderLayout.CENTER);

        final JPanel panelComponent = new JPanel(new BorderLayout());
        panelComponent.setBorder(new TitledBorder("ComponentPaint"));
        panelComponent.setPreferredSize(new Dimension(500, 500));
        panelComponent.add(whiteNoiseComponent.getComponent(), BorderLayout.CENTER);

        final JFrame frame = new JFrame();
        frame.setTitle("WhiteNoiseDemo");
        // frame.setLayout(new GridBagLayout());
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.add(panelCanvas);
        frame.add(panelComponent);

        // frame.setSize(1000, 500);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                ((JFrame) event.getSource()).setVisible(false);
                ((JFrame) event.getSource()).dispose();
                scheduledExecutorService.shutdownNow();
                System.exit(0);
            }
        });
        frame.setVisible(true);
        frame.toFront();

        scheduledExecutorService.scheduleWithFixedDelay(whiteNoiseCanvas, 0, 100, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(whiteNoiseComponent, 0, 100, TimeUnit.MILLISECONDS);
    }
}
