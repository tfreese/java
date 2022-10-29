package de.freese.sonstiges.dnd.color.textfield;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * An implementation of TransferHandler that adds support for dropping colors.<br>
 * Dropping a dnd color on a component having this TransferHandler changes the<br>
 * foreground or the background of the component to the dropped dnd color, according to the value of the changesForegroundColor property.
 */
class ColorTransferHandler extends TransferHandler
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8161007208324025782L;
    /**
     *
     */
    final String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.awt.Color";
    /**
     * The data type exported from JColorChooser.
     */
    DataFlavor colorFlavor;
    /**
     *
     */
    private boolean changesForegroundColor = true;

    /**
     * Creates a new ColorTransferHandler object.
     */
    ColorTransferHandler()
    {
        super();

        // Try to create a DataFlavor for dnd.color.
        try
        {
            this.colorFlavor = new DataFlavor(this.mimeType);
        }
        catch (ClassNotFoundException ex)
        {
            // Empty
        }
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        return hasColorFlavor(flavors);
    }

    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent c, final Transferable t)
    {
        if (hasColorFlavor(t.getTransferDataFlavors()))
        {
            try
            {
                Color col = (Color) t.getTransferData(this.colorFlavor);

                if (hasChangesForegroundColor())
                {
                    c.setForeground(col);
                }
                else
                {
                    c.setBackground(col);
                }

                return true;
            }
            catch (UnsupportedFlavorException ex)
            {
                System.out.println("importData: unsupported data flavor");
            }
            catch (IOException ex)
            {
                System.out.println("importData: I/O exception");
            }
        }

        return false;
    }

    /**
     * @return boolean
     */
    protected boolean hasChangesForegroundColor()
    {
        return this.changesForegroundColor;
    }

    /**
     * Does the flavor list have a Color flavor?
     *
     * @param flavors {@link DataFlavor}[]
     *
     * @return boolean
     */
    protected boolean hasColorFlavor(final DataFlavor[] flavors)
    {
        if (this.colorFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (this.colorFlavor.equals(flavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param flag boolean
     */
    protected void setChangesForegroundColor(final boolean flag)
    {
        this.changesForegroundColor = flag;
    }
}
