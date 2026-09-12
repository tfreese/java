package de.freese.sonstiges.particle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Objects;
import java.util.Random;

/**
 * @author Thomas Freese
 * @since 04.10.2018
 */
class Particle {
    private final Color color;

    private int x;
    private int y;

    Particle(final int initialX, final int initialY, final Color color) {
        super();

        this.color = Objects.requireNonNull(color, "color required");

        x = initialX;
        y = initialY;
    }

    public void draw(final Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, 50, 50);
    }

    public synchronized void move(final Random random) {
        x += random.nextInt(-1, +2);
        y += random.nextInt(-1, +2);
    }
}
