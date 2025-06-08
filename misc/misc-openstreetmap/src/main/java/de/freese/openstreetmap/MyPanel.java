package de.freese.openstreetmap;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serial;

import javax.swing.JPanel;

import de.freese.openstreetmap.model.OsmModel;
import de.freese.openstreetmap.model.OsmWay;

/**
 * @author Thomas Freese
 */
public class MyPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -1375597059859723042L;

    private final transient OsmModel model;
    private Rectangle mapBounds;
    private transient Matrix myZTFMatrix;

    public MyPanel(final OsmModel model) {
        super();

        this.model = model;
    }

    public Rectangle getMapBounds() {
        if (mapBounds == null) {
            for (OsmWay osmWay : model.getWayMap().values()) {
                if (mapBounds == null) {
                    mapBounds = new Rectangle(osmWay.getBounds());
                    continue;
                }

                mapBounds = mapBounds.union(osmWay.getBounds());
            }
            // for (OsmRelation osmRelation : model.getRelationMap().valueCollection()) {
            // if (mapBounds == null) {
            // mapBounds = new Rectangle(osmRelation.getBounds());
            // continue;
            // }
            //
            // mapBounds = mapBounds.union(osmRelation.getBounds());
            // }
        }

        return mapBounds;
    }

    @Override
    public void paint(final Graphics g) {
        if (model.getWayMap().isEmpty() || myZTFMatrix == null) {
            return;
        }

        for (OsmWay osmWay : model.getWayMap().values()) {
            final Polygon polyToDraw = osmWay.getDrawablePolygon(myZTFMatrix);
            g.drawPolygon(polyToDraw);
        }
        // for (OsmRelation osmRelation : model.getRelationMap().valueCollection()) {
        // final Polygon polyToDraw = osmRelation.getDrawablePolygon(myZTFMatrix);
        // g.drawPolygon(polyToDraw);
        // }
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param delta Die Strecke, um die verschoben werden soll
     */
    public void scrollHorizontal(final int delta) {
        final Matrix transMat = Matrix.translate(delta, 0);
        myZTFMatrix = transMat.multiply(myZTFMatrix);
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param delta Die Strecke, um die verschoben werden soll
     */
    public void scrollVertical(final int delta) {
        final Matrix transMat = Matrix.translate(0, delta);
        myZTFMatrix = transMat.multiply(myZTFMatrix);
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass in das Zentrum des Anzeigebereiches herein- bzw. herausgezoomt wird
     *
     * @param factor Der Faktor um den herein- bzw. herausgezoomt wird
     */
    public void zoom(final double factor) {
        final Point center = new Point(getWidth() / 2, getHeight() / 2);
        myZTFMatrix = Matrix.zoomPoint(myZTFMatrix, center, factor);

        // final Matrix scaleMat = Matrix.scale(factor);
        // myZTFMatrix = scaleMat.multiply(myZTFMatrix);
    }

    /**
     * Stellt intern eine Transformationsmatrix zur Verfügung, die so skaliert, verschiebt und spiegelt, dass die zu zeichnenden Polygone komplett in den
     * Anzeigebereich passen
     */
    public void zoomToFit() {
        final Rectangle bounds = getMapBounds();
        myZTFMatrix = Matrix.zoomToFit(bounds, new Rectangle(getWidth() - 2, getHeight() - 2));
    }
}
