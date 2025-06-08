// Created: 04.10.2018
package de.freese.sonstiges.particle;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public final class ParticleFrameMain extends JFrame {
    @Serial
    private static final long serialVersionUID = 6280027925982262751L;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> showGui(10));
    }

    private static void showGui(final int numOfParticles) {
        final ParticleFrameMain frame = new ParticleFrameMain();
        frame.initAndShowGUI();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                frame.stop();
                frame.shutdown();
                frame.dispose();

                System.exit(0);
            }
        });

        frame.start(numOfParticles);
    }

    private final ParticleCanvas canvas;

    private ParticleFrameMain() {
        super();

        canvas = new ParticleCanvas();
        canvas.setSize(new Dimension(800, 800));

        setTitle("Particles");
    }

    public void initAndShowGUI() {
        add(canvas);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void shutdown() {
        canvas.shutdown();
    }

    void start(final int numOfParticles) {
        canvas.start(numOfParticles);
    }

    void stop() {
        canvas.stop();
    }
}
