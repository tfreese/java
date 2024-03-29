// Created: 06.11.2011
package de.freese.openstreetmap;

import javax.swing.SwingUtilities;

import de.freese.openstreetmap.io.OSMParser;
import de.freese.openstreetmap.io.XMLStreamOSMParser;
import de.freese.openstreetmap.model.OsmModel;

/**
 * @author Thomas Freese
 */
public final class OpenStreetMapMain {
    public static void main(final String[] args) {
        try {
            // final OSMParser parser = new JdomOSMParser();
            // final OSMParser parser = new SaxOSMParser();
            final OSMParser parser = new XMLStreamOSMParser();

            final OsmModel model = parser.parse("braunschweig.zip", "braunschweig.osm");
            // final OsmModel model = parser.parse("xapibeispiel.zip", "xapibeispiel.osm");
            System.out.printf("Nodes = %d, Ways = %d, Relations = %d%n", model.getNodeMap().size(), model.getWayMap().size(), model.getRelationMap().size());

            final MyFrame myFrame = new MyFrame(model);
            myFrame.initGui();
            myFrame.setSize(800, 800);
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

            SwingUtilities.invokeLater(myFrame::zoomToFit);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private OpenStreetMapMain() {
        super();
    }
}
