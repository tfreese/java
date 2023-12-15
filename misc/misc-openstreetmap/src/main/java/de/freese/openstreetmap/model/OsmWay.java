// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import de.freese.openstreetmap.Matrix;
import de.freese.openstreetmap.Mercator;

/**
 * Gruppiert {@link OsmNode} zu einem zusammenhängenden Weg.
 *
 * @author Thomas Freese
 */
public class OsmWay extends AbstractOsmEntity {
    private Rectangle bounds;
    private List<OsmNode> nodes;
    private Polygon polygon;

    public Rectangle getBounds() {
        if (this.bounds == null) {
            for (OsmNode osmNode : getNodes()) {
                final double x = Mercator.mercX(osmNode.getLongitude());
                final double y = Mercator.mercY(osmNode.getLatitude());

                if (this.bounds == null) {
                    this.bounds = new Rectangle();
                    this.bounds.x = (int) x;
                    this.bounds.y = (int) y;
                }

                this.bounds.add(x, y);
            }
        }

        return this.bounds;
    }

    public Polygon getDrawablePolygon(final Matrix myZTFMatrix) {
        if (this.polygon == null) {
            this.polygon = new Polygon();

            for (OsmNode osmNode : getNodes()) {
                final double x = Mercator.mercX(osmNode.getLongitude());
                final double y = Mercator.mercY(osmNode.getLatitude());
                this.polygon.addPoint((int) x, (int) y);
            }
        }

        return myZTFMatrix.multiply(this.polygon);
    }

    public List<OsmNode> getNodes() {
        if (this.nodes == null) {
            this.nodes = new ArrayList<>();
        }

        return this.nodes;
    }
}
