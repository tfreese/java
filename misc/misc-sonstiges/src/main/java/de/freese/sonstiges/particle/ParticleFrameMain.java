// Created: 04.10.2018
package de.freese.sonstiges.particle;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public final class ParticleFrameMain extends JFrame
{
    @Serial
    private static final long serialVersionUID = 6280027925982262751L;

    public static void main(final String[] args)
    {
        int numOfParticles = 10;

        //        ParticleFrameMain frame = new ParticleFrameMain();
        //        frame.initAndShowGUI();
        //
        //        frame.addWindowListener(new WindowAdapter()
        //        {
        //            /**
        //             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
        //             */
        //            @Override
        //            public void windowClosing(final WindowEvent e)
        //            {
        //                frame.stop();
        //                frame.shutdown();
        //                frame.dispose();
        //
        //                System.exit(0);
        //            }
        //        });

        SwingUtilities.invokeLater(() -> showGui(numOfParticles));
    }

    private static void showGui(int numOfParticles)
    {
        ParticleFrameMain frame = new ParticleFrameMain();
        frame.initAndShowGUI();

        frame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                frame.stop();
                frame.shutdown();
                frame.dispose();

                System.exit(0);
            }
        });

        frame.start(numOfParticles);
    }

    private final ParticleCanvas canvas;

    private ParticleFrameMain()
    {
        super();

        this.canvas = new ParticleCanvas(800);

        setTitle("Particles");
    }

    public void initAndShowGUI()
    {
        add(this.canvas);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void shutdown()
    {
        this.canvas.shutdown();
    }

    void start(final int numOfParticles)
    {
        this.canvas.start(numOfParticles);
    }

    void stop()
    {
        this.canvas.stop();
    }
}
