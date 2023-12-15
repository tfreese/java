package net.ledticker;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.led.elements.Element;

/**
 * @author Thomas Freese
 */
public class ImageProvider {
    private final Component component;
    private final Element element;
    private final Matrix matrix;
    private final Object object;

    private Image image;

    public ImageProvider(final Element element, final Matrix matrix, final Component component) {
        super();

        this.object = new Object();
        this.element = element;
        this.matrix = matrix;
        this.component = component;

        createImage();
    }

    public void createImage() {
        final int width = this.matrix.getWidthOf(this.element);
        final int height = this.matrix.getHeight();

        if ((this.image == null) || (this.image.getWidth(null) != width) || (this.image.getHeight(null) != height) || (this.image instanceof BufferedImage)) {
            this.image = this.component.createImage(width, height);

            if (this.image == null) {
                this.image = new BufferedImage(width, height, 2);
            }
        }

        if (this.image != null) {
            final Graphics g = this.image.getGraphics();
            this.matrix.paintDots(g, this.image.getWidth(null), this.image.getHeight(null));
            this.matrix.paintElement(g, this.element);
        }
    }

    public Element getElement() {
        return this.element;
    }

    public Image getImage() {
        return this.image;
    }

    public Object getObject() {
        return this.object;
    }
}
