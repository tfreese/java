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

    @Override
    protected void cleanup(final JComponent c, final boolean remove) {
        final JTable source = (JTable) c;

        if (remove && this.rows != null) {
            final DefaultTableModel model = (DefaultTableModel) source.getModel();

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

    @Override
    protected String exportString(final JComponent c) {
        final JTable table = (JTable) c;
        this.rows = table.getSelectedRows();

        final int colCount = table.getColumnCount();

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.rows.length; i++) {
            for (int j = 0; j < colCount; j++) {
                final Object val = table.getValueAt(this.rows[i], j);
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

    @Override
    protected void importString(final JComponent c, final String str) {
        final JTable target = (JTable) c;
        final DefaultTableModel model = (DefaultTableModel) target.getModel();
        int index = target.getSelectedRow();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving rows #4,#5,#6 and #7 and
        // attempts to insert the rows after row #5, this would
        // be problematic when removing the original rows.
        // So this is not allowed.
        if (this.rows != null && index >= (this.rows[0] - 1) && index <= this.rows[this.rows.length - 1]) {
            this.rows = null;

            return;
        }

        final int max = model.getRowCount();

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

        final String[] values = str.split("\n");
        this.addCount = values.length;

        final int colCount = target.getColumnCount();

        for (int i = 0; i < values.length && i < colCount; i++) {
            model.insertRow(index++, values[i].split(","));
        }
    }
}
