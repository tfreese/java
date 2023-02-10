// Created: 31.08.2004
package de.freese.sonstiges.dnd.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * TransferHandler für DnD von JTree-JTree
 *
 * @author Thomas Freese
 */
class TreeTransferHandler extends TransferHandler {
    @Serial
    private static final long serialVersionUID = -3761501619688863055L;
    /**
     * Wird in der Methode exportDone benötigt, damit ein Knoten, der auf sich selbst kopiert wird, nicht gelöscht wird.
     */
    private TreePath targetPath;

    /**
     * Ist DnD erlaubt ?
     *
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors) {
        for (DataFlavor transferFlavor : transferFlavors) {
            if (TransferableTreeNode.TREE_PATH_FLAVOR.equals(transferFlavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Was darf DnD ?
     *
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    /**
     * Kopieren der Transferdaten.
     *
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent comp, final Transferable t) {
        if (canImport(comp, t.getTransferDataFlavors()) && (comp instanceof JTree targetTree)) {
            // Wird in exportDone gegen den sourcePath geprüft
            this.targetPath = targetTree.getSelectionPath();

            TreePath sourcePath = getSourcePath(t);

            if (sourcePath == null) {
                return false;
            }

            DefaultTreeModel targetModell = (DefaultTreeModel) targetTree.getModel();
            DefaultMutableTreeNode targetParent = (DefaultMutableTreeNode) this.targetPath.getLastPathComponent();

            // sourcePath in das Target einfügen, bei Move wird in der Methode exportDone gelöscht, wenn sourcePath != _targetPath ist.
            targetModell.insertNodeInto((MutableTreeNode) sourcePath.getLastPathComponent(), targetParent, targetParent.getChildCount());

            return true;
        }

        return false;
    }

    /**
     * Erzeugen der Transferdaten.
     *
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c) {
        if (!(c instanceof JTree sourceTree)) {
            return null;

            // oder ???
            // return new TransferableTreeNode(null);
        }

        return new TransferableTreeNode(sourceTree.getSelectionPath());
    }

    /**
     * Abschluss des Kopiervorgangs.
     *
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent source, final Transferable data, final int action) {
        TreePath sourcePath = getSourcePath(data);

        if ((action == MOVE) && (source instanceof JTree sourceTree) && (sourcePath != null)) {
            DefaultTreeModel sourceModell = (DefaultTreeModel) sourceTree.getModel();

            // Nur Löschen, wenn Source != Target ist.
            // Kopien auf sich selbst sind erlaubt
            if (this.targetPath != sourcePath) {
                sourceModell.removeNodeFromParent((MutableTreeNode) sourcePath.getLastPathComponent());
            }
        }
    }

    /**
     * ServiceMethode, liefert den SourcePath aus dem Transferable-Daten.
     */
    private TreePath getSourcePath(final Transferable t) {
        TreePath sourcePath = null;

        try {
            if ((t == null) || !(t.getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR) instanceof TreePath)) {
                return null;
            }

            sourcePath = (TreePath) t.getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
        }
        catch (UnsupportedFlavorException | IOException ex) {
            // Ignore
        }

        return sourcePath;
    }
}
