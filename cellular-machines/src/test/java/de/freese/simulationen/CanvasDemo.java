// Created: 26.01.2014
package de.freese.simulationen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.io.Serial;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public class CanvasDemo extends JComponent implements Runnable
{
    @Serial
    private static final long serialVersionUID = -6167704609710052731L;

    public static void main(final String[] args)
    {
        CanvasDemo canvas = new CanvasDemo(100, 100);

        JFrame frame = new JFrame();
        frame.setTitle("TestFrame");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(canvas);
        // frame.pack();
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

        frame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                ((JFrame) e.getSource()).setVisible(false);
                ((JFrame) e.getSource()).dispose();
                scheduledExecutorService.shutdownNow();
                System.exit(0);
            }
        });
        frame.setVisible(true);
        frame.toFront();

        scheduledExecutorService.scheduleWithFixedDelay(canvas, 0, 100, TimeUnit.MILLISECONDS);
    }

    private final Image image;

    private final MemoryImageSource imageSource;

    private final int[] pixelsRGB;

    private final Random random;

    public CanvasDemo(final int width, final int height)
    {
        super();

        setPreferredSize(new Dimension(width, height));

        this.pixelsRGB = new int[width * height];
        this.imageSource = new MemoryImageSource(width, height, this.pixelsRGB, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);
        this.image = createImage(this.imageSource);

        this.random = new Random();
        // setDoubleBuffered(true);
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        // g.drawImage(this.image, 0, 0, null);
        g.drawImage(this.image, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        IntUnaryOperator generator = i ->
        {
            if (this.random.nextBoolean())
            {
                return Color.BLACK.getRGB();
            }

            return Color.WHITE.getRGB();
        };

        // for (int y = 0; y < getImageHeight(); y++)
        // {
        // for (int x = 0; x < getImageWidth(); x++)
        // {
        // this.pixelsRGB[x + (y * getImageWidth())] = generator.applyAsInt(0);
        // }
        // }
        // for (int i = 0; i < this.pixelsRGB.length; i++)
        // {
        // this.pixelsRGB[i] = generator.applyAsInt(0);
        // }
        Arrays.parallelSetAll(this.pixelsRGB, generator);

        this.imageSource.newPixels();
        repaint();
    }

    /**
     * Liefert die Höhe des Bildes, und NICHT die der {@link JComponent}.
     */
    int getImageHeight()
    {
        return getPreferredSize().height;
    }

    /**
     * Liefert die Breite des Bildes, und NICHT die der {@link JComponent}.
     */
    int getImageWidth()
    {
        return getPreferredSize().width;
    }
}
