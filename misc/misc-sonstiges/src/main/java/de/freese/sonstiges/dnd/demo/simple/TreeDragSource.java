// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class TreeDragSource implements DragSourceListener, DragGestureListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreeDragSource.class);

    // private final DragGestureRecognizer recognizer;
    private final DragSource source;
    private final JTree sourceTree;

    private DefaultMutableTreeNode oldNode;

    TreeDragSource(final JTree tree, final int actions) {
        super();

        this.sourceTree = tree;

        source = new DragSource();
        // recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
    }

    @Override
    public void dragDropEnd(final DragSourceDropEvent event) {
        // to support move or copy, we have to check which occurred:
        LOGGER.info("Drop Action: {}", event.getDropAction());

        if (event.getDropSuccess() && event.getDropAction() == DnDConstants.ACTION_MOVE) {
            ((DefaultTreeModel) sourceTree.getModel()).removeNodeFromParent(oldNode);
        }

        // to support move only... if (event.getDropSuccess()) { ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
    }

    @Override
    public void dragEnter(final DragSourceDragEvent event) {
        // Empty
    }

    @Override
    public void dragExit(final DragSourceEvent event) {
        // Empty
    }

    @Override
    public void dragGestureRecognized(final DragGestureEvent event) {
        final TreePath path = sourceTree.getSelectionPath();

        if (path == null || path.getPathCount() <= 1) {
            // We can't move the root node or an empty selection
            return;
        }

        oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        final TransferableTreeNode transferable = new TransferableTreeNode(path);
        source.startDrag(event, DragSource.DefaultMoveNoDrop, transferable, this);

        // If you support dropping the node anywhere, you should probably
        // start with a valid move cursor:
        // source.startDrag(event, DragSource.DefaultMoveDrop, transferable, this);
    }

    @Override
    public void dragOver(final DragSourceDragEvent event) {
        // Empty
    }

    @Override
    public void dropActionChanged(final DragSourceDragEvent event) {
        LOGGER.info("Action: {}", event.getDropAction());
        LOGGER.info("Target Action: {}", event.getTargetActions());
        LOGGER.info("User Action: {}", event.getUserAction());
    }
}
