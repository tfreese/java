// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class TreeDropTarget implements DropTargetListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreeDropTarget.class);

    // private final DropTarget target;
    // private final JTree targetTree;

    TreeDropTarget(final JTree tree) {
        super();

        // targetTree = tree;
        // target = new DropTarget(targetTree, this);
    }

    @Override
    public void dragEnter(final DropTargetDragEvent event) {
        final TreeNode node = getNodeForEvent(event);

        if (node.isLeaf()) {
            event.rejectDrag();
        }
        else {
            // start by supporting move operations
            // event.acceptDrag(DnDConstants.ACTION_MOVE);
            event.acceptDrag(event.getDropAction());
        }
    }

    @Override
    public void dragExit(final DropTargetEvent event) {
        // Empty
    }

    @Override
    public void dragOver(final DropTargetDragEvent event) {
        final TreeNode node = getNodeForEvent(event);

        if (node.isLeaf()) {
            event.rejectDrag();
        }
        else {
            // start by supporting move operations
            // event.acceptDrag(DnDConstants.ACTION_MOVE);
            event.acceptDrag(event.getDropAction());
        }
    }

    @Override
    public void drop(final DropTargetDropEvent event) {
        final Point pt = event.getLocation();
        final DropTargetContext dtc = event.getDropTargetContext();
        final JTree tree = (JTree) dtc.getComponent();
        final TreePath parentPath = tree.getClosestPathForLocation(pt.x, pt.y);
        final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

        if (parent.isLeaf()) {
            event.rejectDrop();

            return;
        }

        try {
            final Transferable tr = event.getTransferable();
            final DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (DataFlavor flavor : flavors) {
                if (tr.isDataFlavorSupported(flavor)) {
                    event.acceptDrop(event.getDropAction());

                    final TreePath p = (TreePath) tr.getTransferData(flavor);
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
                    final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.insertNodeInto(node, parent, 0);
                    event.dropComplete(true);

                    return;
                }
            }

            event.rejectDrop();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            event.rejectDrop();
        }
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent event) {
        // Empty
    }

    private TreeNode getNodeForEvent(final DropTargetDragEvent event) {
        final Point p = event.getLocation();
        final DropTargetContext dtc = event.getDropTargetContext();
        final JTree tree = (JTree) dtc.getComponent();
        final TreePath path = tree.getClosestPathForLocation(p.x, p.y);

        return (TreeNode) path.getLastPathComponent();
    }
}
