// Created: 31.08.2004
package de.freese.sonstiges.dnd.tree;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.Serial;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testklasse für DnD von JTree - JTree.
 *
 * @author Thomas Freese
 */
public final class TreeDragMain extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreeDragMain.class);
    @Serial
    private static final long serialVersionUID = 1197758766045157541L;

    public static void main(final String[] args) {
        new TreeDragMain();
    }

    private TreeDragMain() {
        super("TreeDragMain");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final TreeTransferHandler transferHandler = new TreeTransferHandler();

        // this.treeLeft = new de.freese.base.swing.components.tree.ExtTree();
        final JTree treeLeft = new JTree();
        treeLeft.setDragEnabled(true);
        treeLeft.setTransferHandler(transferHandler);

        try {
            treeLeft.getDropTarget().addDropTargetListener(new TreeDropTargetListener());
        }
        catch (TooManyListenersException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        final JScrollPane spLeft = new JScrollPane(treeLeft);
        spLeft.setPreferredSize(new Dimension(200, 400));

        // this.treeRight = new de.freese.base.swing.components.tree.ExtTree();
        final JTree treeRight = new JTree();
        treeRight.setDragEnabled(true);
        treeRight.setTransferHandler(transferHandler);

        final JScrollPane spRight = new JScrollPane(treeRight);
        spRight.setPreferredSize(new Dimension(200, 400));

        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(spLeft);
        getContentPane().add(spRight);

        pack();
        setResizable(false);
        setVisible(true);

        setLocationRelativeTo(null);
    }
}
