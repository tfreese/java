// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
class TransferableTreeNode implements Transferable
{
    public static final DataFlavor FLAVOR_TREE_PATH = new DataFlavor(TreePath.class, "Tree Path");

    private static final DataFlavor[] FLAVORS =
            {
                    FLAVOR_TREE_PATH
            };

    private final TreePath path;

    TransferableTreeNode(final TreePath tp)
    {
        this.path = tp;
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (isDataFlavorSupported(flavor))
        {
            return this.path;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
    public synchronized DataFlavor[] getTransferDataFlavors()
    {
        return FLAVORS;
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor)
    {
        return (flavor.getRepresentationClass() == TreePath.class);
    }
}
