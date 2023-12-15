// Created: 09.02.2014
package de.freese.sonstiges.attraktor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.MemoryImageSource;
import java.io.Serial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public class TestAttraktor extends JComponent implements Runnable {
    @Serial
    private static final long serialVersionUID = 1852796219960955003L;

    public static void main(final String[] args) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

        final JFrame frame = new JFrame();
        frame.setTitle("TestFrame");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                scheduledExecutorService.shutdownNow();
            }
        });

        final TestAttraktor canvas = new TestAttraktor();
        frame.add(canvas);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // canvas.run();
        scheduledExecutorService.scheduleWithFixedDelay(canvas, 1000, 500, TimeUnit.MILLISECONDS);
    }

    private final transient Image image;
    private final transient MemoryImageSource imageSource;
    private final int[] pixels;
    private final transient Random random;

    private double x = 200D;
    private double y = 200D;

    public TestAttraktor() {
        super();

        final int width = 800;
        final int height = width;

        this.pixels = new int[width * height];
        this.imageSource = new MemoryImageSource(width, height, this.pixels, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);
        this.image = createImage(this.imageSource);

        this.random = new Random();
        setDoubleBuffered(true);

        this.x = 200;
        this.y = 200;
    }

    @Override
    public void paint(final Graphics g) {
        // g.drawImage(this.image, 0, 0, null);
        g.drawImage(this.image, 0, 0, getWidth(), getHeight(), null);
    }

    @Override
    public void run() {
        final Set<Point2D> points = new HashSet<>();

        final double a = -14D;
        final double b = 0.9F;
        final double c = 0.1D;

        final Point2D min = new Point2D.Double(0, 0);
        final Point2D max = new Point2D.Double(0, 0);

        for (int i = 0; i < 10; i++) {
            final double xx = this.y - (Math.signum(this.x) * Math.sqrt(Math.abs((b * this.x) - c)));
            // final double xx = this.y - (FastMath.signum(this.x) * FastMath.pow(Math.abs((b * this.x) - c), 0.5D));
            final double yy = a - this.x;
            this.x = xx;
            this.y = yy;

            final Point2D point = new Point2D.Double(this.x, this.y);
            points.add(point);

            min.setLocation(Math.min(min.getX(), point.getX()), Math.min(min.getY(), point.getY()));
            max.setLocation(Math.max(max.getX(), point.getX()), Math.max(max.getY(), point.getY()));
        }

        // System.out.println("Points: " + points.size());
        // System.out.println("Min: " + min);
        // System.out.println("Max: " + max);
        // System.out.println("Pixel: " + (Math.abs(min.getX()) + max.getX()) + " x " + (Math.abs(min.getY()) + max.getY()));

        // Arrays.fill(this.pixels, Color.WHITE.getRGB());
        Arrays.parallelSetAll(this.pixels, i -> Color.WHITE.getRGB());

        for (Point2D point : points) {
            this.x = point.getX() + 400;
            this.y = point.getY() + 400;
            this.pixels[(int) (this.x + (this.y * 800))] = Color.BLACK.getRGB();
        }

        this.imageSource.newPixels();
        repaint();
    }

    Random getRandom() {
        return this.random;
    }
}
