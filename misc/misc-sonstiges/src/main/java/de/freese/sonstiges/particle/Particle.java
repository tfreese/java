package de.freese.sonstiges.particle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * @author Thomas Freese
 */
class Particle {
    private final Color color;
    private final Random random;
    
    private int x;
    private int y;

    Particle(final Random random, final int initialX, final int initialY) {
        super();

        this.random = random;
        x = initialX;
        y = initialY;

        // color = new Color(random.nextInt(0xFFFFFF));
        color = new Color(random.nextInt(Integer.MAX_VALUE));
    }

    public void draw(final Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, 10, 10);
    }

    public synchronized void move() {
        x += random.nextInt(10) - 5;
        y += random.nextInt(10) - 5;
    }
}
