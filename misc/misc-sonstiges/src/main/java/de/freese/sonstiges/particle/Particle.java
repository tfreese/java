package de.freese.sonstiges.particle;

import java.awt.Graphics;
import java.util.Random;

/**
 * @author Thomas Freese
 */
class Particle
{
    protected final Random random;

    protected int x;

    protected int y;

    Particle(final Random random, final int initialX, final int initialY)
    {
        super();

        this.random = random;
        this.x = initialX;
        this.y = initialY;
    }

    public void draw(final Graphics g)
    {
        int lx;
        int ly;

        synchronized (this)
        {
            lx = this.x;
            ly = this.y;
        }

        g.drawRect(lx, ly, 10, 10);
    }

    public synchronized void move()
    {
        this.x += (this.random.nextInt(10) - 5);
        this.y += (this.random.nextInt(10) - 5);
    }
}
