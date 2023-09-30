package net.ledticker;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

/**
 * @author Thomas Freese
 */
public interface LedTicker {
    void addElement(Element tickerElement);

    JComponent getTickerComponent();

    void pauseAnimation();

    void removeAll();

    void removeElement(Element tickerElement);

    void setBackgroundColor(Color color);

    void setDotGaps(int hGap, int vGap);

    void setDotOffColor(Color color);

    void setDotSize(int dotWidth, int dotHeight);

    void setElementGap(int elementGap);

    void setSpeed(int speed);

    void setTokenGap(int tokenGap);

    void startAnimation();

    void stopAnimation();

    void update(Element tickerElement);

    void updateAll();
}
