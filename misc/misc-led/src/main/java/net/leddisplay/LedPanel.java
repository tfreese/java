package net.leddisplay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.JPanel;

import net.led.elements.Element;

/**
 * @author Thomas Freese
 */
public class LedPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 3000L;

    private final transient Matrix matrix;

    private transient Element displayElement;

    private int height;

    private Dimension preferredSize;

    public LedPanel(final Matrix matrix) {
        super();

        this.matrix = matrix;

        setBackground(null);
        setLayout(null);
        setDoubleBuffered(true);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dimension = super.getPreferredSize();

        if (this.preferredSize == null) {
            Insets insets = getInsets();
            dimension = new Dimension(dimension.width + 399, this.height + insets.top + insets.bottom);
        }

        return dimension;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        this.matrix.paintDots(g, getWidth(), getHeight());

        if (this.displayElement == null) {
            return;
        }

        this.matrix.paint(g, this.displayElement, getWidth(), getHeight());
    }

    public void setDisplayElement(final Element newValue) {
        this.displayElement = newValue;

        repaint();
    }

    public void setHeight(final int newValue) {
        this.height = newValue;
    }

    @Override
    public void setPreferredSize(final Dimension dimension) {
        this.preferredSize = dimension;
        super.setPreferredSize(dimension);
    }
}
