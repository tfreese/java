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

    private transient final OsmModel model;

    private Rectangle mapBounds;

    private transient Matrix myZTFMatrix;

    public MyPanel(final OsmModel model) {
        super();

        this.model = model;
    }

    public Rectangle getMapBounds() {
        if (this.mapBounds == null) {
            for (OsmWay osmWay : this.model.getWayMap().values()) {
                if (this.mapBounds == null) {
                    this.mapBounds = new Rectangle(osmWay.getBounds());
                    continue;
                }

                this.mapBounds = this.mapBounds.union(osmWay.getBounds());
            }
            // for (OsmRelation osmRelation : this.model.getRelationMap().valueCollection())
            // {
            // if (this.mapBounds == null)
            // {
            // this.mapBounds = new Rectangle(osmRelation.getBounds());
            // continue;
            // }
            //
            // this.mapBounds = this.mapBounds.union(osmRelation.getBounds());
            // }
        }

        return this.mapBounds;
    }

    @Override
    public void paint(final Graphics g) {
        if (this.model.getWayMap().isEmpty() || (this.myZTFMatrix == null)) {
            return;
        }

        for (OsmWay osmWay : this.model.getWayMap().values()) {
            Polygon polyToDraw = osmWay.getDrawablePolygon(this.myZTFMatrix);
            g.drawPolygon(polyToDraw);
        }
        // for (OsmRelation osmRelation : this.model.getRelationMap().valueCollection())
        // {
        // Polygon polyToDraw = osmRelation.getDrawablePolygon(this.myZTFMatrix);
        // g.drawPolygon(polyToDraw);
        // }
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param delta Die Strecke, um die verschoben werden soll
     */
    public void scrollHorizontal(final int delta) {
        Matrix transMat = Matrix.translate(delta, 0);
        this.myZTFMatrix = transMat.multiply(this.myZTFMatrix);
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param delta Die Strecke, um die verschoben werden soll
     */
    public void scrollVertical(final int delta) {
        Matrix transMat = Matrix.translate(0, delta);
        this.myZTFMatrix = transMat.multiply(this.myZTFMatrix);
    }

    /**
     * Verändert die interne Transformationsmatrix so, dass in das Zentrum des Anzeigebereiches herein- bzw. herausgezoomt wird
     *
     * @param factor Der Faktor um den herein- bzw. herausgezoomt wird
     */
    public void zoom(final double factor) {
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        this.myZTFMatrix = Matrix.zoomPoint(this.myZTFMatrix, center, factor);

        // Matrix scaleMat = Matrix.scale(factor);
        // this.myZTFMatrix = scaleMat.multiply(this.myZTFMatrix);
    }

    /**
     * Stellt intern eine Transformationsmatrix zur Verfügung, die so skaliert, verschiebt und spiegelt, dass die zu zeichnenden Polygone komplett in den
     * Anzeigebereich passen
     */
    public void zoomToFit() {
        Rectangle bounds = getMapBounds();
        this.myZTFMatrix = Matrix.zoomToFit(bounds, new Rectangle(getWidth() - 2, getHeight() - 2));
    }
}
