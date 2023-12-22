// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Objects;

import de.freese.led.encoder.SymbolEncoder;
import de.freese.led.model.element.LedElement;
import de.freese.led.model.token.ArrowToken;
import de.freese.led.model.token.LedToken;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLedPainter implements LedPainter {
    private final SymbolEncoder symbolEncoder;

    private Color backgroundColor = new Color(17, 17, 17);
    private int dotHeight = 2;
    private Color dotOfflineColor = new Color(102, 102, 102);
    private int dotWidth = 2;
    private int hGap = 1;
    private int vGap = 1;

    protected AbstractLedPainter(final SymbolEncoder symbolEncoder) {
        super();

        this.symbolEncoder = Objects.requireNonNull(symbolEncoder, "symbolEncoder required");
    }

    @Override
    public void paintElement(final Graphics graphics, final LedElement ledElement, final int width, final int height) {
        final LedToken[] tokens = ledElement.getTokens();

        int x = getDotWidth() + gethGap();
        final int y = getDotHeight() + getvGap();

        for (LedToken token : tokens) {
            final int tokenWidth = paintToken(graphics, token, x, y);
            x += tokenWidth;

            if (x > width) {
                return;
            }
        }
    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setDotHeight(final int dotHeight) {
        this.dotHeight = dotHeight;
    }

    @Override
    public void setDotOfflineColor(final Color dotOfflineColor) {
        this.dotOfflineColor = dotOfflineColor;
    }

    @Override
    public void setDotWidth(final int dotWidth) {
        this.dotWidth = dotWidth;
    }

    @Override
    public void sethGap(final int hGap) {
        this.hGap = hGap;
    }

    @Override
    public void setvGap(final int vGap) {
        this.vGap = vGap;
    }

    protected Color getBackgroundColor() {
        return backgroundColor;
    }

    protected int getDotHeight() {
        return dotHeight;
    }

    protected Color getDotOfflineColor() {
        return dotOfflineColor;
    }

    protected int getDotWidth() {
        return dotWidth;
    }

    protected int gethGap() {
        return hGap;
    }

    protected int getvGap() {
        return vGap;
    }

    protected abstract void paintDot(Graphics graphics, int x, int y, int width, int height);

    protected int paintSymbol(final Graphics graphics, final int[] encodedSymbol, final int x, final int y) {
        int xOffset = 0;
        int yOffset = 0;

        for (int code : encodedSymbol) {
            xOffset = 0;

            for (int xx = 0; xx < symbolEncoder.getHorizontalDots(); xx++) {
                if ((code & (1 << xx)) != 0) {
                    paintDot(graphics, x + xOffset, y + yOffset, getDotWidth(), getDotHeight());
                }

                xOffset += getDotWidth() + gethGap();
            }

            yOffset += getDotHeight() + getvGap();
        }

        return xOffset + getDotWidth();
    }

    protected int paintToken(final Graphics graphics, final LedToken ledToken, final int x, final int y) {
        graphics.setColor(ledToken.getColor());

        final String value = ledToken.getValue();
        int xOffset = 0;
        final int yOffset = 0;

        if (ledToken instanceof ArrowToken arrowToken) {
            final int[] encodedSymbol = symbolEncoder.getEncoded(arrowToken.getArrow());
            final int symbolWidth = paintSymbol(graphics, encodedSymbol, x + xOffset, y + yOffset);

            xOffset += symbolWidth + gethGap();
        }
        else {
            for (int i = 0; i < value.length(); i++) {
                final char symbol = value.charAt(i);
                final int[] encodedSymbol = symbolEncoder.getEncoded(symbol);
                final int symbolWidth = paintSymbol(graphics, encodedSymbol, x + xOffset, y + yOffset);

                xOffset += symbolWidth + gethGap();
            }
        }

        return xOffset;
    }
}
