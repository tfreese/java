package net.leddisplay;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

/**
 * @author Thomas Freese
 */
public interface LedDisplay {
    int CENTER = 0;

    int EAST = 3;

    int NORTH = 1;

    int NORTHEAST = 6;

    int NORTHWEST = 5;

    int SOUTH = 4;

    int SOUTHEAST = 8;

    int SOUTHWEST = 7;

    int WEST = 2;

    JComponent getComponent();

    void setAnchor(int anchor);

    void setBackgroundColor(Color color);

    void setDisplayElement(Element element);

    void setDotGaps(int hGap, int vGap);

    void setDotOffColor(Color color);

    void setDotSize(int width, int height);

    void setPadding(int top, int left, int bottom, int right);

    void setTokenGap(int gap);

    void update();
}
