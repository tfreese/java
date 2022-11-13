// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
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

/**
 * @author Thomas Freese
 */
class TreeDropTarget implements DropTargetListener
{
    private final DropTarget target;

    private final JTree targetTree;

    TreeDropTarget(final JTree tree)
    {
        this.targetTree = tree;
        this.target = new DropTarget(this.targetTree, this);
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragEnter(final DropTargetDragEvent event)
    {
        TreeNode node = getNodeForEvent(event);

        if (node.isLeaf())
        {
            event.rejectDrag();
        }
        else
        {
            // start by supporting move operations
            // event.acceptDrag(DnDConstants.ACTION_MOVE);
            event.acceptDrag(event.getDropAction());
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    @Override
    public void dragExit(final DropTargetEvent event)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragOver(final DropTargetDragEvent event)
    {
        TreeNode node = getNodeForEvent(event);

        if (node.isLeaf())
        {
            event.rejectDrag();
        }
        else
        {
            // start by supporting move operations
            // event.acceptDrag(DnDConstants.ACTION_MOVE);
            event.acceptDrag(event.getDropAction());
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @Override
    public void drop(final DropTargetDropEvent event)
    {
        Point pt = event.getLocation();
        DropTargetContext dtc = event.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath parentPath = tree.getClosestPathForLocation(pt.x, pt.y);
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

        if (parent.isLeaf())
        {
            event.rejectDrop();

            return;
        }

        try
        {
            Transferable tr = event.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (DataFlavor flavor : flavors)
            {
                if (tr.isDataFlavorSupported(flavor))
                {
                    event.acceptDrop(event.getDropAction());

                    TreePath p = (TreePath) tr.getTransferData(flavor);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.insertNodeInto(node, parent, 0);
                    event.dropComplete(true);

                    return;
                }
            }

            event.rejectDrop();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            event.rejectDrop();
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(final DropTargetDragEvent event)
    {
        // Empty
    }

    private TreeNode getNodeForEvent(final DropTargetDragEvent event)
    {
        Point p = event.getLocation();
        DropTargetContext dtc = event.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath path = tree.getClosestPathForLocation(p.x, p.y);

        return (TreeNode) path.getLastPathComponent();
    }
}
