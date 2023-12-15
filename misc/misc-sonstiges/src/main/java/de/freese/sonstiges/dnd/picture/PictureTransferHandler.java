package de.freese.sonstiges.dnd.picture;

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
    @Serial
    private static final long serialVersionUID = 8896193065899268221L;

    /**
     * @author Thomas Freese
     */
    class PictureTransferable implements Transferable {
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
            return new DataFlavor[]{PictureTransferHandler.this.pictureFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return PictureTransferHandler.this.pictureFlavor.equals(flavor);
        }
    }

    private final DataFlavor pictureFlavor = DataFlavor.imageFlavor;
    private boolean shouldRemove;
    private DTPicture sourcePic;

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (this.pictureFlavor.equals(flavor)) {
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
                image = (Image) t.getTransferData(this.pictureFlavor);

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
