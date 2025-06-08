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
class TransferableTreeNode implements Transferable {
    public static final DataFlavor FLAVOR_TREE_PATH = new DataFlavor(TreePath.class, "Tree Path");
    private static final DataFlavor[] FLAVORS = {FLAVOR_TREE_PATH};

    private final TreePath path;

    TransferableTreeNode(final TreePath tp) {
        super();

        this.path = tp;
    }

    @Override
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return path;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.getRepresentationClass() == TreePath.class;
    }
}
