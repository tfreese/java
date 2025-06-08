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
        if (bounds == null) {
            for (OsmNode osmNode : getNodes()) {
                final double x = Mercator.mercX(osmNode.getLongitude());
                final double y = Mercator.mercY(osmNode.getLatitude());

                if (bounds == null) {
                    bounds = new Rectangle();
                    bounds.x = (int) x;
                    bounds.y = (int) y;
                }

                bounds.add(x, y);
            }
        }

        return bounds;
    }

    public Polygon getDrawablePolygon(final Matrix myZTFMatrix) {
        if (polygon == null) {
            polygon = new Polygon();

            for (OsmNode osmNode : getNodes()) {
                final double x = Mercator.mercX(osmNode.getLongitude());
                final double y = Mercator.mercY(osmNode.getLatitude());
                polygon.addPoint((int) x, (int) y);
            }
        }

        return myZTFMatrix.multiply(polygon);
    }

    public List<OsmNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }

        return nodes;
    }
}
