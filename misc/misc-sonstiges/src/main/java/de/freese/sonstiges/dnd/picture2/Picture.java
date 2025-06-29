package de.freese.sonstiges.dnd.picture2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

import javax.accessibility.Accessible;
import javax.swing.JComponent;

/**
 * @author Thomas Freese
 */
class Picture extends JComponent implements MouseListener, FocusListener, Accessible {
    @Serial
    private static final long serialVersionUID = 4762065978744789032L;

    private transient Image image;

    Picture(final Image image) {
        super();

        this.image = image;

        setFocusable(true);
        addMouseListener(this);
        addFocusListener(this);
    }

    @Override
    public void focusGained(final FocusEvent event) {
        // Draw the component with a red border indicating that it has focus.
        repaint();
    }

    @Override
    public void focusLost(final FocusEvent event) {
        // Draw the component with a black border indicating that it doesn't have focus.
        repaint();
    }

    @Override
    public void mouseClicked(final MouseEvent event) {
        // Since the user clicked on us, let's get focus!
        requestFocusInWindow();
    }

    @Override
    public void mouseEntered(final MouseEvent event) {
        // Empty
    }

    @Override
    public void mouseExited(final MouseEvent event) {
        // Empty
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        // Empty
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
        // Empty
    }

    Image getImage() {
        return image;
    }

    void setImage(final Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics g = graphics.create();

        // Draw in our entire space, even if isOpaque is false.
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (image == null) ? 125 : image.getWidth(this), (image == null) ? 125 : image.getHeight(this));

        if (image != null) {
            // Draw image at its natural size of 125x125.
            g.drawImage(image, 0, 0, this);
        }

        // Add a border, red if picture currently has focus
        if (isFocusOwner()) {
            g.setColor(Color.RED);
        }
        else {
            g.setColor(Color.BLACK);
        }

        g.drawRect(0, 0, (image == null) ? 125 : image.getWidth(this), (image == null) ? 125 : image.getHeight(this));
        g.dispose();
    }
}
