// Created: 29.06.2003
package de.freese.sonstiges.particle;

import java.awt.Canvas;
import java.awt.Graphics;
import java.io.Serial;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class ParticleCanvas extends Canvas {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleCanvas.class);

    @Serial
    private static final long serialVersionUID = -7875942028557880029L;

    private final Random random = new SecureRandom();
    private final transient ScheduledExecutorService scheduledExecutorService;

    private transient ScheduledFuture<?> future;
    private transient List<Particle> particles = Collections.emptyList();

    ParticleCanvas() {
        super();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void paint(final Graphics g) {
        particles.forEach(p -> p.draw(g));
    }

    public synchronized void shutdown() {
        stop();

        LOGGER.info("ParticleCanvas.shutdown() ...");

        scheduledExecutorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks.
                scheduledExecutorService.shutdownNow();

                // Wait a while for tasks to respond to being cancelled.
                if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex) {
            // (Re-)Cancel if current thread also interrupted.
            scheduledExecutorService.shutdownNow();

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }

        LOGGER.info("ParticleCanvas.shutdown() ... finished");
    }

    public synchronized void start(final int numOfParticles) {
        LOGGER.info("ParticleCanvas.start()");

        if (future != null) {
            future.cancel(true);
            future = null;
        }

        particles = new ArrayList<>(numOfParticles);

        for (int i = 0; i < numOfParticles; ++i) {
            particles.add(new Particle(random, 400, 300));
        }

        future = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            particles.forEach(Particle::move);
            repaint();
        }, 250, 250, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        LOGGER.info("ParticleCanvas.stop()");

        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }
}
