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
class Picture extends JComponent implements MouseListener, FocusListener, Accessible
{
    @Serial
    private static final long serialVersionUID = 4762065978744789032L;

    private Image image;

    Picture(final Image image)
    {
        super();

        this.image = image;

        setFocusable(true);
        addMouseListener(this);
        addFocusListener(this);
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(final FocusEvent e)
    {
        // Draw the component with a red border
        // indicating that it has focus.
        this.repaint();
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(final FocusEvent e)
    {
        // Draw the component with a black border
        // indicating that it doesn't have focus.
        this.repaint();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        // Since the user clicked on us, let's get focus!
        requestFocusInWindow();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        // Empty
    }

    Image getImage()
    {
        return this.image;
    }

    void setImage(final Image image)
    {
        this.image = image;
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics graphics)
    {
        Graphics g = graphics.create();

        // Draw in our entire space, even if isOpaque is false.
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (this.image == null) ? 125 : this.image.getWidth(this), (this.image == null) ? 125 : this.image.getHeight(this));

        if (this.image != null)
        {
            // Draw image at its natural size of 125x125.
            g.drawImage(this.image, 0, 0, this);
        }

        // Add a border, red if picture currently has focus
        if (isFocusOwner())
        {
            g.setColor(Color.RED);
        }
        else
        {
            g.setColor(Color.BLACK);
        }

        g.drawRect(0, 0, (this.image == null) ? 125 : this.image.getWidth(this), (this.image == null) ? 125 : this.image.getHeight(this));
        g.dispose();
    }
}
