package de.freese.sonstiges.dnd.demo.image;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;

import javax.swing.tree.TreePath;

/**
 * This represents a TreePath (a node in a JTree) that can be transferred between a drag source and a drop target.
 */
class CTransferableTreePath implements Transferable {
    public static final DataFlavor FLAVOR_TREE_PATH = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

    // public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor(TreePath.class, "TreePath");

    private static final DataFlavor[] FLAVORS = {FLAVOR_TREE_PATH};

    private final TreePath path;

    CTransferableTreePath(final TreePath path) {
        super();

        this.path = path;
    }

    @Override
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.isMimeTypeEqual(FLAVOR_TREE_PATH.getMimeType())) // DataFlavor.javaJVMLocalObjectMimeType))
        {
            return path;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return List.of(FLAVORS).contains(flavor);
    }
}
