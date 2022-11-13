// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
class TreeDragSource implements DragSourceListener, DragGestureListener
{
    private final DragGestureRecognizer recognizer;

    private final DragSource source;

    private final JTree sourceTree;

    private DefaultMutableTreeNode oldNode;

    TreeDragSource(final JTree tree, final int actions)
    {
        super();

        this.sourceTree = tree;
        this.source = new DragSource();
        this.recognizer = this.source.createDefaultDragGestureRecognizer(this.sourceTree, actions, this);
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
     */
    @Override
    public void dragDropEnd(final DragSourceDropEvent event)
    {
        //to support move or copy, we have to check which occurred:
        System.out.println("Drop Action: " + event.getDropAction());

        if (event.getDropSuccess() && (event.getDropAction() == DnDConstants.ACTION_MOVE))
        {
            ((DefaultTreeModel) this.sourceTree.getModel()).removeNodeFromParent(this.oldNode);
        }

        // to support move only... if (event.getDropSuccess()) { ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dragEnter(final DragSourceDragEvent event)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
     */
    @Override
    public void dragExit(final DragSourceEvent event)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
     */
    @Override
    public void dragGestureRecognized(final DragGestureEvent event)
    {
        TreePath path = this.sourceTree.getSelectionPath();

        if ((path == null) || (path.getPathCount() <= 1))
        {
            // We can't move the root node or an empty selection
            return;
        }

        this.oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        TransferableTreeNode transferable = new TransferableTreeNode(path);
        this.source.startDrag(event, DragSource.DefaultMoveNoDrop, transferable, this);

        // If you support dropping the node anywhere, you should probably
        // start with a valid move cursor:
        // source.startDrag(event, DragSource.DefaultMoveDrop, transferable, this);
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dragOver(final DragSourceDragEvent event)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dropActionChanged(final DragSourceDragEvent event)
    {
        System.out.println("Action: " + event.getDropAction());
        System.out.println("Target Action: " + event.getTargetActions());
        System.out.println("User Action: " + event.getUserAction());
    }
}
