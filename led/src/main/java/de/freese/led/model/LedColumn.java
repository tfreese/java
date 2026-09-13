package de.freese.led.model;

import java.awt.Color;
import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
final class LedColumn {
    private final Color colorActive;
    private final boolean[] leds;

    LedColumn(final boolean[] leds) {
        this(leds, null);
    }

    LedColumn(final boolean[] leds, final Color colorActive) {
        super();

        this.leds = Objects.requireNonNull(leds, "leds required");
        this.colorActive = colorActive;
    }

    Color getColorActive() {
        return colorActive;
    }

    boolean isActive(final int row) {
        return leds[row];
    }
}
