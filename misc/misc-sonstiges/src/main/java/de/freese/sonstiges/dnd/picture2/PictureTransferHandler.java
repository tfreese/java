package de.freese.sonstiges.dnd.picture2;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * @author Thomas Freese
 */
class PictureTransferHandler extends TransferHandler {
    private static final DataFlavor PICTURE_FLAVOR = DataFlavor.imageFlavor;
    @Serial
    private static final long serialVersionUID = 7183662667049793445L;

    /**
     * @author Thomas Freese
     */
    static class PictureTransferable implements Transferable {
        private final Image image;

        PictureTransferable(final DTPicture pic) {
            this.image = pic.getImage();
        }

        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return this.image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{PICTURE_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return PICTURE_FLAVOR.equals(flavor);
        }
    }

    private boolean shouldRemove;
    private DTPicture sourcePic;

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (PICTURE_FLAVOR.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        final Image image;

        if (canImport(c, t.getTransferDataFlavors())) {
            final DTPicture pic = (DTPicture) c;

            // Don't drop on myself.
            if (this.sourcePic == pic) {
                this.shouldRemove = false;

                return true;
            }

            try {
                image = (Image) t.getTransferData(PICTURE_FLAVOR);

                // Set the component to the new picture.
                pic.setImage(image);

                return true;
            }
            catch (UnsupportedFlavorException ufe) {
                System.out.println("importData: unsupported data flavor");
            }
            catch (IOException ioe) {
                System.out.println("importData: I/O exception");
            }
        }

        return false;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        this.sourcePic = (DTPicture) c;
        this.shouldRemove = true;

        return new PictureTransferable(this.sourcePic);
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (this.shouldRemove && (action == MOVE)) {
            this.sourcePic.setImage(null);
        }

        this.sourcePic = null;
    }
}
