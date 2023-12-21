// Created: 21.12.23
package de.freese.led.painter;

import java.awt.Color;
import java.awt.Graphics;

import de.freese.led.model.element.LedElement;

/**
 * @author Thomas Freese
 */
public interface LedPainter {
    void paintElement(Graphics graphics, LedElement ledElement, int width, int height);

    void paintOfflineDots(Graphics graphics, int width, int height);

    void setBackgroundColor(Color backgroundColor);

    void setDotHeight(int dotHeight);

    void setDotOfflineColor(Color dotOfflineColor);

    void setDotWidth(int dotWidth);

    void sethGap(int hGap);

    void setvGap(int vGap);
}
