package de.freese.led2;

import java.awt.Color;
import java.awt.Font;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
public final class LedModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -2751656423937836829L;

    private static final class LedColumn {
        private final Color colorActive;
        private final boolean[] leds;

        private LedColumn(final boolean[] leds) {
            this(leds, null);
        }

        private LedColumn(final boolean[] leds, final Color colorActive) {
            super();

            this.leds = Objects.requireNonNull(leds, "leds required");
            this.colorActive = colorActive;
        }

        public Color getColorActive() {
            return colorActive;
        }

        boolean isActive(final int row) {
            return leds[row];
        }
    }

    private final transient List<LedColumn> ledColumns = new ArrayList<>();
    private Color colorActive = new Color(255, 30, 30);
    private Color colorBackground = Color.BLACK;
    private Color colorInactive = new Color(40, 10, 10);

    public LedModel addColumn(final boolean[] leds) {
        ledColumns.add(new LedColumn(leds));

        return this;
    }

    public LedModel addColumn(final boolean[] leds, final Color colorActive) {
        ledColumns.add(new LedColumn(leds, colorActive));

        return this;
    }

    public LedModel addText(final String text, final int matrixHeight) {
        return addText(text, matrixHeight, new Font(Font.MONOSPACED, Font.BOLD, matrixHeight - 2));
    }

    public LedModel addText(final String text, final int matrixHeight, final Font font) {
        final boolean[][] matrix = LedFontConverter.convertTextToLED(text, matrixHeight, font);

        for (final boolean[] column : matrix) {
            addColumn(column);
        }

        return this;
    }

    public int columnCount() {
        return ledColumns.size();
    }

    public Color getColorActive(final int column) {
        final Color color = ledColumns.get(column).getColorActive();

        return (color != null) ? color : getColorActive();
    }

    public Color getColorActive() {
        return colorActive;
    }

    public Color getColorBackground() {
        return colorBackground;
    }

    public Color getColorInactive() {
        return colorInactive;
    }

    public boolean isActive(final int row, final int column) {
        return ledColumns.get(column).isActive(row);
    }

    public LedModel setColorActive(final Color colorActive) {
        this.colorActive = Objects.requireNonNull(colorActive, "colorActive required");

        return this;
    }

    public LedModel setColorBackground(final Color colorBackground) {
        this.colorBackground = Objects.requireNonNull(colorBackground, "colorBackground required");

        return this;
    }

    public LedModel setColorInactive(final Color colorInactive) {
        this.colorInactive = Objects.requireNonNull(colorInactive, "colorInactive required");

        return this;
    }
}
