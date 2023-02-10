package de.freese.sonstiges.dnd.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

/**
 * Transferdaten-Object für DnD von JTree<->JTree.
 *
 * @author Thomas Freese
 */
class TransferableTreeNode implements Transferable {
    /**
     * Datentyp-Definition
     */
    static final DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class, "Tree Path");

    private final DataFlavor[] flavors = {TREE_PATH_FLAVOR};

    private final TreePath path;

    TransferableTreeNode(final TreePath tp) {
        this.path = tp;
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this.path;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return this.flavors;
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return (flavor.getRepresentationClass() == TreePath.class);
    }
}
