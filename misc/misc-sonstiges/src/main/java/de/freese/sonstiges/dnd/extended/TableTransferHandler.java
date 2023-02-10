package de.freese.sonstiges.dnd.extended;

import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author Thomas Freese
 */
class TableTransferHandler extends StringTransferHandler {
    @Serial
    private static final long serialVersionUID = 8631829448750837938L;
    /**
     * Number of items added.
     */
    private int addCount;
    /**
     * Location where items were added
     */
    private int addIndex = -1;

    private int[] rows;

    /**
     * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#cleanup(javax.swing.JComponent, boolean)
     */
    @Override
    protected void cleanup(final JComponent c, final boolean remove) {
        JTable source = (JTable) c;

        if (remove && (this.rows != null)) {
            DefaultTableModel model = (DefaultTableModel) source.getModel();

            // If we are moving items around in the same table, we
            // need to adjust the rows accordingly, since those
            // after the insertion point have moved.
            if (this.addCount > 0) {
                for (int i = 0; i < this.rows.length; i++) {
                    if (this.rows[i] > this.addIndex) {
                        this.rows[i] += this.addCount;
                    }
                }
            }

            for (int i = this.rows.length - 1; i >= 0; i--) {
                model.removeRow(this.rows[i]);
            }
        }

        this.rows = null;
        this.addCount = 0;
        this.addIndex = -1;
    }

    /**
     * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#exportString(javax.swing.JComponent)
     */
    @Override
    protected String exportString(final JComponent c) {
        JTable table = (JTable) c;
        this.rows = table.getSelectedRows();

        int colCount = table.getColumnCount();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.rows.length; i++) {
            for (int j = 0; j < colCount; j++) {
                Object val = table.getValueAt(this.rows[i], j);
                sb.append((val == null) ? "" : val.toString());

                if (j != (colCount - 1)) {
                    sb.append(",");
                }
            }

            if (i != (this.rows.length - 1)) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#importString(javax.swing.JComponent, java.lang.String)
     */
    @Override
    protected void importString(final JComponent c, final String str) {
        JTable target = (JTable) c;
        DefaultTableModel model = (DefaultTableModel) target.getModel();
        int index = target.getSelectedRow();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving rows #4,#5,#6 and #7 and
        // attempts to insert the rows after row #5, this would
        // be problematic when removing the original rows.
        // So this is not allowed.
        if ((this.rows != null) && (index >= (this.rows[0] - 1)) && (index <= this.rows[this.rows.length - 1])) {
            this.rows = null;

            return;
        }

        int max = model.getRowCount();

        if (index < 0) {
            index = max;
        }
        else {
            index++;

            if (index > max) {
                index = max;
            }
        }

        this.addIndex = index;

        String[] values = str.split("\n");
        this.addCount = values.length;

        int colCount = target.getColumnCount();

        for (int i = 0; (i < values.length) && (i < colCount); i++) {
            model.insertRow(index++, values[i].split(","));
        }
    }
}
