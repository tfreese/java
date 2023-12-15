// Created: 30.08.2004
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * @author Thomas Freese
 */
public final class TreeDragMain extends JFrame {
    @Serial
    private static final long serialVersionUID = -1040887315433480625L;

    public static void main(final String[] args) {
        new TreeDragMain();
    }

    private transient final TreeDragSource ds;
    private transient final TreeDropTarget dt;

    private final JTree tree;

    private TreeDragMain() {
        super("Rearrange able Tree");

        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // If you want AutoScrolling, use this line:
        // this.tree = new de.freese.base.swing.components.tree.ExtTree();
        this.tree = new JTree();

        // Otherwise, use this line:
        // tree = new JTree();
        getContentPane().add(new JScrollPane(this.tree), BorderLayout.CENTER);

        // If we only support move operations...
        // ds = new TreeDragSource(tree, DnDConstants.ACTION_MOVE);
        this.ds = new TreeDragSource(this.tree, DnDConstants.ACTION_COPY_OR_MOVE);
        this.dt = new TreeDropTarget(this.tree);
        setVisible(true);
    }
}
