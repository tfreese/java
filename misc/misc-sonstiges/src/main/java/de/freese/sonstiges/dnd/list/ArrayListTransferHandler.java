package de.freese.sonstiges.dnd.list;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
class ArrayListTransferHandler extends TransferHandler {
    private static final String LOCAL_ARRAY_LIST_TYPE = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.ArrayList";
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayListTransferHandler.class);
    @Serial
    private static final long serialVersionUID = -5057942587610930744L;

    /**
     * @author Thomas Freese
     */
    public class ArrayListTransferable implements Transferable {
        private final List<?> data;

        ArrayListTransferable(final List<?> data) {
            super();

            this.data = data;
        }

        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{ArrayListTransferHandler.this.localArrayListFlavor, ArrayListTransferHandler.this.serialArrayListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return ArrayListTransferHandler.this.localArrayListFlavor.equals(flavor) || ArrayListTransferHandler.this.serialArrayListFlavor.equals(flavor);
        }
    }

    private final DataFlavor localArrayListFlavor;
    private final DataFlavor serialArrayListFlavor;

    /**
     * Number of items added
     */
    private int addCount;
    /**
     * Location where items were added
     */
    private int addIndex = -1;
    private int[] indices;
    private JList<?> source;

    ArrayListTransferHandler() throws ClassNotFoundException {
        super();

        localArrayListFlavor = new DataFlavor(LOCAL_ARRAY_LIST_TYPE);
        serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
    }

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        return hasLocalArrayListFlavor(flavors) || hasSerialArrayListFlavor(flavors);
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        JList<?> target = null;
        List<?> list = null;

        try {
            target = (JList<?>) c;

            if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
                list = (List<?>) t.getTransferData(localArrayListFlavor);
            }
            else if (hasSerialArrayListFlavor(t.getTransferDataFlavors())) {
                list = (List<?>) t.getTransferData(serialArrayListFlavor);
            }
            else {
                return false;
            }
        }
        catch (UnsupportedFlavorException _) {
            LOGGER.error("importData: unsupported data flavor");

            return false;
        }
        catch (IOException _) {
            LOGGER.error("importData: I/O exception");

            return false;
        }

        // At this point we use the same code to retrieve the data
        // locally or serially.
        // We'll drop at the current selected index.
        int index = target.getSelectedIndex();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving items #4,#5,#6 and #7 and
        // attempts to insert the items after item #5, this would
        // be problematic when removing the original items.
        // This is interpreted as dropping the same data on itself
        // and has no effect.
        if (source.equals(target)) {
            if (indices != null && index >= (indices[0] - 1) && index <= indices[indices.length - 1]) {
                indices = null;

                return true;
            }
        }

        final DefaultListModel<Object> listModel = (DefaultListModel<Object>) target.getModel();
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

        addIndex = index;
        addCount = list.size();

        for (Object element : list) {
            listModel.add(index, element);
            index++;
        }

        return true;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        if (c instanceof JList) {
            source = (JList<?>) c;
            indices = source.getSelectedIndices();

            final List<?> values = source.getSelectedValuesList();

            if (values == null || values.isEmpty()) {
                return null;
            }

            final List<String> list = new ArrayList<>(values.size());

            for (Object o : values) {
                String str = o.toString();

                if (str == null) {
                    str = "";
                }

                list.add(str);
            }

            return new ArrayListTransferable(list);
        }

        return null;
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (action == MOVE && indices != null) {
            final DefaultListModel<?> model = (DefaultListModel<?>) source.getModel();

            // If we are moving items around in the same list, we
            // need to adjust the indices accordingly since those
            // after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] > addIndex) {
                        indices[i] += addCount;
                    }
                }
            }

            for (int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }

        indices = null;
        addIndex = -1;
        addCount = 0;
    }

    private boolean hasLocalArrayListFlavor(final DataFlavor[] flavors) {
        if (localArrayListFlavor == null) {
            return false;
        }

        for (DataFlavor flavor : flavors) {
            if (flavor.equals(localArrayListFlavor)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasSerialArrayListFlavor(final DataFlavor[] flavors) {
        if (serialArrayListFlavor == null) {
            return false;
        }

        for (DataFlavor flavor : flavors) {
            if (flavor.equals(serialArrayListFlavor)) {
                return true;
            }
        }

        return false;
    }
}
