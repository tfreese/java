// Created: 29.06.2003
package de.freese.sonstiges.particle;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
class ParticleCanvas extends Canvas
{
    @Serial
    private static final long serialVersionUID = -7875942028557880029L;

    private final Random random = new Random();

    private transient final ScheduledExecutorService scheduledExecutorService;

    private transient ScheduledFuture<?> future;

    private transient List<Particle> particles = Collections.emptyList();

    ParticleCanvas()
    {
        this(800);
    }

    ParticleCanvas(final int size)
    {
        setSize(new Dimension(size, size));

        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    /**
     * @see java.awt.Canvas#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        this.particles.forEach(p -> p.draw(g));
    }

    public synchronized void shutdown()
    {
        stop();

        System.out.println("ParticleCanvas.shutdown() ...");

        this.scheduledExecutorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!this.scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                this.scheduledExecutorService.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!this.scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS))
                {
                    System.err.println("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            // (Re-)Cancel if current thread also interrupted
            this.scheduledExecutorService.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        System.out.println("ParticleCanvas.shutdown() ... finished");
    }

    public synchronized void start(final int numOfParticles)
    {
        System.out.println("ParticleCanvas.start()");

        if (future != null)
        {
            future.cancel(true);
            future = null;
        }

        this.particles = new ArrayList<>(numOfParticles);

        for (int i = 0; i < numOfParticles; ++i)
        {
            this.particles.add(new Particle(this.random, 400, 300));
        }

        future = scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            this.particles.forEach(Particle::move);
            this.repaint();
        }, 250, 250, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop()
    {
        System.out.println("ParticleCanvas.stop()");

        if (future != null)
        {
            future.cancel(true);
            future = null;
        }
    }
}
