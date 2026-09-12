package de.freese.sonstiges.particle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 12.09.26
 */
@SuppressWarnings({"java:S2095"})
public class ParticleDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleDemo.class);

    private static final Random RANDOM = new SecureRandom();

    static void main() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

        final List<Particle> particles = getColors().stream().map(color -> new Particle(400, 400, color)).toList();

        final JPanel paintingComponent = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                synchronized (particles) {
                    particles.forEach(p -> p.draw(g));
                }

                // Synchronizing the painting on systems that buffer graphics events.
                // Without this line, the animation might not be smooth on Linux.
                Toolkit.getDefaultToolkit().sync();
            }

            @Override
            protected void printChildren(final Graphics g) {
                // There are no Children.
                // super.printChildren(g);
            }
        };

        // 60 FPS = 1000ms / 60 = 16,6ms
        final long delay = 16L;

        final ScheduledFuture<?> futureModel = scheduledExecutorService.scheduleWithFixedDelay(() ->
                particles.forEach(p -> p.move(RANDOM)), 250L, delay, TimeUnit.MILLISECONDS);

        final ScheduledFuture<?> futurePaint = scheduledExecutorService.scheduleWithFixedDelay(() ->
                SwingUtilities.invokeLater(paintingComponent::repaint), 250L, delay, TimeUnit.MILLISECONDS);

        final JFrame frame = new JFrame("ParticleDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                stop(futureModel);
                stop(futurePaint);
                shutdown(scheduledExecutorService);
                frame.dispose();

                System.exit(0);
            }
        });

        frame.add(paintingComponent);
        frame.setSize(new Dimension(800, 800));
        // frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
    }

    private static List<Color> getColors() {
        final List<Color> colors = new ArrayList<>();

        // final Color color = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
        // final Color color = new Color(random.nextInt(0xFFFFFF));
        // final Color color = new Color(RANDOM.nextInt(Integer.MAX_VALUE));

        colors.add(Color.BLACK);
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.DARK_GRAY);
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);
        colors.add(Color.ORANGE);
        colors.add(Color.PINK);
        colors.add(Color.RED);

        return colors;
    }

    private static void shutdown(final ScheduledExecutorService scheduledExecutorService) {
        LOGGER.info("shutdown ...");

        scheduledExecutorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!scheduledExecutorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks.
                scheduledExecutorService.shutdownNow();

                // Wait a while for tasks to respond to being canceled.
                if (!scheduledExecutorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    LOGGER.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException _) {
            // (Re-)Cancel if current thread also interrupted.
            scheduledExecutorService.shutdownNow();

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }

        LOGGER.info("shutdown ... finished");
    }

    private static void stop(final ScheduledFuture<?> future) {
        LOGGER.info("stop future ...");

        future.cancel(true);

        LOGGER.info("stop future ... finished");
    }
}
