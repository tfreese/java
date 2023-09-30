package net.ledticker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.led.elements.Element;
import net.led.tokens.TextToken;
import net.led.tokens.Token;

/**
 * @author Thomas Freese
 */
public class DefaultLedTicker implements LedTicker {
    /**
     * @author Thomas Freese
     */
    private static class DefaultElement implements Element {
        private final Token[] array;

        DefaultElement() {
            super();

            TextToken texttoken = new TextToken("WWW.LEDTICKER.NET::");

            this.array = (new Token[]{texttoken});
        }

        @Override
        public Token[] getTokens() {
            return this.array;
        }

        public void setDotOffColor(final Color newValue) {
            Color color = new Color(newValue.getRGB() ^ 16_777_215);
            this.array[0].getColorModel().setColor(color);
        }
    }

    private final List<ImageProvider> elements;

    private final DefaultElement h;

    private final LedPanel ledPanel;

    private final Matrix matrix;

    public DefaultLedTicker() {
        super();

        this.h = new DefaultElement();
        this.elements = new ArrayList<>();
        this.matrix = new Matrix();
        this.ledPanel = new LedPanel();
        this.ledPanel.setHeight(this.matrix.getHeight());

        addElement(this.h);
    }

    @Override
    public void addElement(final Element tickerElement) {
        ImageProvider imageProvider = new ImageProvider(tickerElement, this.matrix, this.ledPanel);
        this.elements.add(imageProvider);
        this.ledPanel.repaint(imageProvider.getImage(), imageProvider.getObject());
    }

    @Override
    public JComponent getTickerComponent() {
        return this.ledPanel;
    }

    @Override
    public void pauseAnimation() {
        this.ledPanel.pauseAnimation();
    }

    @Override
    public void removeAll() {
        this.elements.clear();
        this.ledPanel.g();

        addElement(this.h);
    }

    @Override
    public void removeElement(final Element tickerElement) {
        for (int i = 0; i < this.elements.size(); i++) {
            ImageProvider imageProvider = this.elements.get(i);

            if (imageProvider.getElement() != tickerElement) {
                continue;
            }

            this.ledPanel.b(imageProvider.getObject());
            this.elements.remove(imageProvider);

            break;
        }
    }

    @Override
    public void setBackgroundColor(final Color color) {
        this.matrix.setBackgroundColor(color);

        updateAll();
    }

    @Override
    public void setDotGaps(final int hGap, final int vGap) {
        this.matrix.setDotGaps(hGap, vGap);
        this.ledPanel.setHeight(this.matrix.getHeight());

        updateAll();
    }

    @Override
    public void setDotOffColor(final Color color) {
        this.h.setDotOffColor(color);
        this.matrix.setDotOffColor(color);

        updateAll();
    }

    @Override
    public void setDotSize(final int dotWidth, final int dotHeight) {
        this.matrix.setDotSize(dotWidth, dotHeight);
        this.ledPanel.setHeight(this.matrix.getHeight());

        updateAll();
    }

    @Override
    public void setElementGap(final int elementGap) {
        this.matrix.setElementGap(elementGap);

        updateAll();
    }

    @Override
    public void setSpeed(final int speed) {
        if ((speed >= 1) && (speed <= 10)) {
            this.ledPanel.setSpeed(13 - speed);
        }
        else {
            throw new IllegalArgumentException("Unsupported speed (" + speed + "). Speed must be between 1 and 10.");
        }
    }

    @Override
    public void setTokenGap(final int tokenGap) {
        this.matrix.setTokenGap(tokenGap);

        updateAll();
    }

    @Override
    public void startAnimation() {
        this.ledPanel.startAnimation();
    }

    @Override
    public void stopAnimation() {
        this.ledPanel.stopAnimation();
    }

    @Override
    public void update(final Element tickerElement) {
        for (ImageProvider imageProvider : this.elements) {
            if (imageProvider.getElement() == tickerElement) {
                imageProvider.createImage();
                this.ledPanel.repaint(imageProvider.getImage(), imageProvider.getObject());

                return;
            }
        }

        throw new IllegalArgumentException("Updated element was not found in the Ticker's element list.");
    }

    @Override
    public void updateAll() {
        this.ledPanel.b(true);
        this.ledPanel.b(this.matrix.getImage());

        for (ImageProvider imageProvider : this.elements) {
            imageProvider.createImage();
            this.ledPanel.repaint(imageProvider.getImage(), imageProvider.getObject());
        }

        this.ledPanel.b(false);
    }
}
