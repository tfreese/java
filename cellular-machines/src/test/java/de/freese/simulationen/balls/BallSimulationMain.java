// Created: 26.09.2016
package de.freese.simulationen.balls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Simulation sich bewegender Bälle mit physikalisch korrektem Verhalten.
 *
 * @author Thomas Freese
 */
public final class BallSimulationMain extends JComponent {
    /**
     * [ms]
     */
    private static final int DELAY = 40;

    @Serial
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        // Dimensionen [m]
        int w = 400;
        int h = 240;

        BallSimulationMain simulation = new BallSimulationMain(w, h);
        simulation.addBall(w / 3D, h / 3D, 20D, 188D, 30D);
        simulation.addBall((2D * w) / 3D, (2D * h) / 3D, -30D, -30D, 30D);

        JFrame frame = new JFrame("Ballsimulation");
        frame.setContentPane(simulation);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

        frame.addWindowListener(new WindowAdapter() {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
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

        final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(simulation::moveAndPaintBalls, 1, DELAY, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (simulation.isFinished()) {
                scheduledFuture.cancel(true);
                scheduledExecutorService.shutdownNow();
                System.exit(0);
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    private transient final List<Ball> balls = new ArrayList<>();

    private transient final BufferedImage image;

    private BallSimulationMain(final int width, final int height) {
        super();

        setPreferredSize(new Dimension(width, height));

        setBackground(Color.WHITE);

        // GraphicsConfiguration gfxConf =
        // GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        // this.image = gfxConf.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // setDoubleBuffered(true);
    }

    /**
     * Hinzufügen eines neuen Balls.
     *
     * @param x Aktuelle X-Koordinate [m].
     * @param y Aktuelle Y-Koordinate [m].
     * @param vx Horizontale Geschwindigkeit [m/s].
     * @param vy Vertikale Geschwindigkeit [m/s].
     * @param durchmesser [m]
     */
    public void addBall(final double x, final double y, final double vx, final double vy, final double durchmesser) {
        Ball ball = new Ball(getImageWidth(), getImageHeight(), x, y, vx, vy, durchmesser, 0.1D);

        if (!this.balls.contains(ball)) {
            this.balls.add(ball);
        }
    }

    /**
     * Liefert true, wenn alle Bälle zum stillstand gekommen sind.
     */
    public boolean isFinished() {
        return this.balls.stream().allMatch(Ball::isFinished);
    }

    /**
     * Bewegen der Bälle und neu zeichnen.
     */
    public void moveAndPaintBalls() {
        Graphics2D g = this.image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // g.setBackground(Color.WHITE);
        // An alter Stelle löschen.
        // this.balls.forEach(b -> paint(g, b, Color.WHITE));
        g.setColor(getBackground());
        g.fillRect(0, 0, getImageWidth(), getImageHeight());

        gitter(g);

        // Bälle bewegen.
        this.balls.forEach(b -> b.move(DELAY * 5));

        // An neuer Stelle malen.
        this.balls.forEach(b -> paint(g, b, Color.BLUE));

        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * @see java.awt.Window#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g) {
        // g.drawImage(this.image, 0, 0, this);
        g.drawImage(this.image, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * Liefert die Höhe des Bildes, und NICHT die der {@link JComponent}.
     */
    private int getImageHeight() {
        return getPreferredSize().height;
    }

    /**
     * Liefert die Breite des Bildes, und NICHT die der {@link JComponent}.
     */
    private int getImageWidth() {
        return getPreferredSize().width;
    }

    private void gitter(final Graphics g) {
        g.setColor(Color.BLACK);

        int stepX = getImageWidth() / 10;
        int stepY = getImageHeight() / 5;

        for (int i = stepX; i <= getImageWidth(); i += stepX) {
            g.drawLine(i, 0, i, getImageHeight());
        }

        for (int i = stepY; i <= getImageHeight(); i += stepY) {
            g.drawLine(0, i, getImageWidth(), i);
        }
    }

    private void paint(final Graphics g, final Ball ball, final Color color) {
        g.setColor(color);
        // g.translate(0, 0);

        // Koordinate umrechnen: 0,0 ist oben links.
        double x = ball.getX() - ball.getRadius();
        double y = ball.getMaxY() - (ball.getY() + ball.getRadius());

        int durchmesser = (int) ball.getDurchmesser();

        g.fillOval((int) x, (int) y, durchmesser, durchmesser);
    }
}
