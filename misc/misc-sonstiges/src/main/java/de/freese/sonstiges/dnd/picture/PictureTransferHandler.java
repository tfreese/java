package de.freese.sonstiges.dnd.picture;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class PictureTransferHandler extends TransferHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureTransferHandler.class);
    private static final DataFlavor PICTURE_FLAVOR = DataFlavor.imageFlavor;
    @Serial
    private static final long serialVersionUID = 8896193065899268221L;

    /**
     * @author Thomas Freese
     */
    static class PictureTransferable implements Transferable {
        private final Image image;

        PictureTransferable(final DTPicture pic) {
            super();

            image = pic.getImage();
        }

        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return image;
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
            if (sourcePic == pic) {
                shouldRemove = false;

                return true;
            }

            try {
                image = (Image) t.getTransferData(PICTURE_FLAVOR);

                // Set the component to the new picture.
                pic.setImage(image);

                return true;
            }
            catch (UnsupportedFlavorException ex) {
                LOGGER.error("importData: unsupported data flavor", ex);
            }
            catch (IOException ex) {
                LOGGER.error("importData: I/O exception", ex);
            }
        }

        return false;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        sourcePic = (DTPicture) c;
        shouldRemove = true;

        return new PictureTransferable(sourcePic);
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (shouldRemove && action == MOVE) {
            sourcePic.setImage(null);
        }

        sourcePic = null;
    }
}
