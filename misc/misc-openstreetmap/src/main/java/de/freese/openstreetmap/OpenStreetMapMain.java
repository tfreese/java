// Created: 06.11.2011
package de.freese.openstreetmap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import de.freese.openstreetmap.io.OSMParser;
import de.freese.openstreetmap.io.XMLStreamOSMParser;
import de.freese.openstreetmap.model.OsmModel;

/**
 * @author Thomas Freese
 */
public final class OpenStreetMapMain {
    private static final Logger LOGGER = Logger.getLogger(OpenStreetMapMain.class.getSimpleName());

    public static void main(final String[] args) {
        try {
            // final OSMParser parser = new JdomOSMParser();
            // final OSMParser parser = new SaxOSMParser();
            final OSMParser parser = new XMLStreamOSMParser();

            final OsmModel model = parser.parse("braunschweig.zip", "braunschweig.osm");
            // final OsmModel model = parser.parse("xapibeispiel.zip", "xapibeispiel.osm");

            final String message = "Nodes = %d, Ways = %d, Relations = %d%n".formatted(model.getNodeMap().size(), model.getWayMap().size(), model.getRelationMap().size());
            LOGGER.log(Level.INFO, message);

            final MyFrame myFrame = new MyFrame(model);
            myFrame.initGui();
            myFrame.setSize(800, 800);
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

            SwingUtilities.invokeLater(myFrame::zoomToFit);
        }
        catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private OpenStreetMapMain() {
        super();
    }
}
