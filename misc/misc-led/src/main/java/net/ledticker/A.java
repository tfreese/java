package net.ledticker;

import java.awt.Image;

/**
 * @author Thomas Freese
 */
public class A {
    private int b;

    private Image image;

    private Image lastImage;

    private int width;

    public A() {
        super();
    }

    public int b() {
        return this.width;
    }

    public void b(final Image image, final boolean flag) {
        if (image == null) {
            this.image = null;
            this.lastImage = null;
            this.width = 0;
            this.b = 0;

            return;
        }

        if (flag) {
            this.image = image;
            this.width = image.getWidth(null);
        }
        else {
            this.lastImage = image;
        }
    }

    public void b(final int i) {
        this.b = i;
    }

    public int c() {
        return this.b;
    }

    public void c(final int i) {
        this.b -= i;
    }

    public void d() {
        if (this.lastImage != null) {
            this.image = this.lastImage;
            this.width = this.lastImage.getWidth(null);
            this.lastImage = null;
            this.b = 0;
        }
    }

    public Image getImage() {
        return this.image;
    }
}
