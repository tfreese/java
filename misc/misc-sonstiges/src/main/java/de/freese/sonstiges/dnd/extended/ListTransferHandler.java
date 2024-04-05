package de.freese.sonstiges.dnd.extended;

import java.io.Serial;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
class ListTransferHandler extends StringTransferHandler {
    @Serial
    private static final long serialVersionUID = -3208151404479849978L;
    /**
     * Number of items added.
     */
    private int addCount;
    /**
     * Location where items were added
     */
    private int addIndex = -1;
    private int[] indices;

    @Override
    protected void cleanup(final JComponent c, final boolean remove) {
        if (remove && this.indices != null) {
            final JList<?> source = (JList<?>) c;
            final DefaultListModel<?> model = (DefaultListModel<?>) source.getModel();

            // If we are moving items around in the same list, we
            // need to adjust the indices accordingly, since those
            // after the insertion point have moved.
            if (this.addCount > 0) {
                for (int i = 0; i < this.indices.length; i++) {
                    if (this.indices[i] > this.addIndex) {
                        this.indices[i] += this.addCount;
                    }
                }
            }

            for (int i = this.indices.length - 1; i >= 0; i--) {
                model.remove(this.indices[i]);
            }
        }

        this.indices = null;
        this.addCount = 0;
        this.addIndex = -1;
    }

    @Override
    protected String exportString(final JComponent c) {
        final JList<?> list = (JList<?>) c;
        this.indices = list.getSelectedIndices();

        final List<?> values = list.getSelectedValuesList();

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            final Object val = values.get(i);
            sb.append((val == null) ? "" : val.toString());

            if (i != (values.size() - 1)) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    protected void importString(final JComponent c, final String str) {
        final JList<String> target = (JList<String>) c;
        final DefaultListModel<String> listModel = (DefaultListModel<String>) target.getModel();
        int index = target.getSelectedIndex();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving items #4,#5,#6 and #7 and
        // attempts to insert the items after item #5, this would
        // be problematic when removing the original items.
        // So this is not allowed.
        if (this.indices != null && index >= (this.indices[0] - 1) && index <= this.indices[this.indices.length - 1]) {
            this.indices = null;

            return;
        }

        final int max = listModel.getSize();

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

        for (String value : values) {
            listModel.add(index++, value);
        }
    }
}
