package net.leddisplay;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

/**
 * @author Thomas Freese
 */
public class DefaultLedDisplay implements LedDisplay {
    private final LedPanel ledPanel;

    private final Matrix matrix;

    public DefaultLedDisplay() {
        super();

        this.matrix = new Matrix();
        this.ledPanel = new LedPanel(this.matrix);
    }

    @Override
    public JComponent getComponent() {
        return this.ledPanel;
    }

    @Override
    public void setAnchor(final int i) {
        this.matrix.setAnchor((i >= 9) ? (i % 9) : i);
        update();
    }

    @Override
    public void setBackgroundColor(final Color color) {
        this.matrix.setBackgroundColor(color);
        update();
    }

    @Override
    public void setDisplayElement(final Element element) {
        this.ledPanel.setDisplayElement(element);
    }

    @Override
    public void setDotGaps(final int i, final int j) {
        this.matrix.setDotGaps(i, j);
        this.ledPanel.setHeight(this.matrix.getHeight());
        update();
    }

    @Override
    public void setDotOffColor(final Color color) {
        this.matrix.setDotOffColor(color);
        update();
    }

    @Override
    public void setDotSize(final int width, final int height) {
        this.matrix.setDotSize(width, height);
        this.ledPanel.setHeight(this.matrix.getHeight());
        update();
    }

    @Override
    public void setPadding(final int top, final int left, final int bottom, final int right) {
        this.matrix.setPadding(top, left, bottom, right);
        update();
    }

    @Override
    public void setTokenGap(final int gap) {
        this.matrix.setTokenGap(gap);
        update();
    }

    @Override
    public void update() {
        this.ledPanel.repaint();
    }
}
