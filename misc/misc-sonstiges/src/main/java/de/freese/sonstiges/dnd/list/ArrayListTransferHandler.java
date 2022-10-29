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

/**
 * @author Thomas Freese
 */
public class ArrayListTransferHandler extends TransferHandler
{
    /**
     *
     */
    private static final String LOCAL_ARRAY_LIST_TYPE = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.ArrayList";
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5057942587610930744L;

    /**
     * @author Thomas Freese
     */
    public class ArrayListTransferable implements Transferable
    {
        /**
         *
         */
        private final List<?> data;

        /**
         *
         */
        public ArrayListTransferable(final List<?> data)
        {
            super();

            this.data = data;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (!isDataFlavorSupported(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }

            return this.data;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]
                    {
                            ArrayListTransferHandler.this.localArrayListFlavor, ArrayListTransferHandler.this.serialArrayListFlavor
                    };
        }

        /**
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor)
        {
            return ArrayListTransferHandler.this.localArrayListFlavor.equals(flavor) || ArrayListTransferHandler.this.serialArrayListFlavor.equals(flavor);
        }
    }

    /**
     *
     */
    private final DataFlavor localArrayListFlavor;
    /**
     *
     */
    private final DataFlavor serialArrayListFlavor;
    /**
     * Number of items added
     */
    private int addCount;
    /**
     * Location where items were added
     */
    private int addIndex = -1;
    /**
     *
     */
    private int[] indices;
    /**
     *
     */
    private JList<?> source;

    /**
     * Creates a new ArrayListTransferHandler object.
     *
     * @throws ClassNotFoundException Falls was schiefgeht.
     */
    public ArrayListTransferHandler() throws ClassNotFoundException
    {
        super();

        this.localArrayListFlavor = new DataFlavor(LOCAL_ARRAY_LIST_TYPE);
        this.serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        return hasLocalArrayListFlavor(flavors) || hasSerialArrayListFlavor(flavors);
    }

    /**
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c)
    {
        return COPY_OR_MOVE;
    }

    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @SuppressWarnings(
            {
                    "rawtypes", "unchecked"
            })
    @Override
    public boolean importData(final JComponent c, final Transferable t)
    {
        if (!canImport(c, t.getTransferDataFlavors()))
        {
            return false;
        }

        JList<?> target = null;
        List list = null;

        try
        {
            target = (JList<?>) c;

            if (hasLocalArrayListFlavor(t.getTransferDataFlavors()))
            {
                list = (List<?>) t.getTransferData(this.localArrayListFlavor);
            }
            else if (hasSerialArrayListFlavor(t.getTransferDataFlavors()))
            {
                list = (List<?>) t.getTransferData(this.serialArrayListFlavor);
            }
            else
            {
                return false;
            }
        }
        catch (UnsupportedFlavorException ex)
        {
            System.out.println("importData: unsupported data flavor");

            return false;
        }
        catch (IOException ex)
        {
            System.out.println("importData: I/O exception");

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
        if (this.source.equals(target))
        {
            if ((this.indices != null) && (index >= (this.indices[0] - 1)) && (index <= this.indices[this.indices.length - 1]))
            {
                this.indices = null;

                return true;
            }
        }

        DefaultListModel listModel = (DefaultListModel) target.getModel();
        int max = listModel.getSize();

        if (index < 0)
        {
            index = max;
        }
        else
        {
            index++;

            if (index > max)
            {
                index = max;
            }
        }

        this.addIndex = index;
        this.addCount = list.size();

        for (Object element : list)
        {
            listModel.add(index, element);
            index++;
        }

        return true;
    }

    /**
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c)
    {
        if (c instanceof JList)
        {
            this.source = (JList<?>) c;
            this.indices = this.source.getSelectedIndices();

            List<?> values = this.source.getSelectedValuesList();

            if ((values == null) || (values.isEmpty()))
            {
                return null;
            }

            List<String> list = new ArrayList<>(values.size());

            for (Object o : values)
            {
                String str = o.toString();

                if (str == null)
                {
                    str = "";
                }

                list.add(str);
            }

            return new ArrayListTransferable(list);
        }

        return null;
    }

    /**
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action)
    {
        if ((action == MOVE) && (this.indices != null))
        {
            DefaultListModel<?> model = (DefaultListModel<?>) this.source.getModel();

            // If we are moving items around in the same list, we
            // need to adjust the indices accordingly since those
            // after the insertion point have moved.
            if (this.addCount > 0)
            {
                for (int i = 0; i < this.indices.length; i++)
                {
                    if (this.indices[i] > this.addIndex)
                    {
                        this.indices[i] += this.addCount;
                    }
                }
            }

            for (int i = this.indices.length - 1; i >= 0; i--)
            {
                model.remove(this.indices[i]);
            }
        }

        this.indices = null;
        this.addIndex = -1;
        this.addCount = 0;
    }

    /**
     * @param flavors {@link DataFlavor}[]
     *
     * @return boolean
     */
    private boolean hasLocalArrayListFlavor(final DataFlavor[] flavors)
    {
        if (this.localArrayListFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (flavor.equals(this.localArrayListFlavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param flavors {@link DataFlavor}[]
     *
     * @return boolean
     */
    private boolean hasSerialArrayListFlavor(final DataFlavor[] flavors)
    {
        if (this.serialArrayListFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (flavor.equals(this.serialArrayListFlavor))
            {
                return true;
            }
        }

        return false;
    }
}
