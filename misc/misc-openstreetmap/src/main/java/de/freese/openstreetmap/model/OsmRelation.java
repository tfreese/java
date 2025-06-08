// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import de.freese.openstreetmap.Matrix;
import de.freese.openstreetmap.Mercator;

/**
 * Relationen gruppieren {@link OsmNode} und {@link OsmWay} zu größeren Entitäten.
 *
 * @author Thomas Freese
 */
public class OsmRelation extends AbstractOsmEntity {
    private Rectangle bounds;
    private List<OsmNode> nodes;
    private Polygon polygon;
    private List<OsmWay> ways;

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

            if (bounds == null) {
                bounds = new Rectangle(0, 0);
            }

            for (OsmWay osmWay : getWays()) {
                bounds = bounds.union(osmWay.getBounds());
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

            for (OsmWay osmWay : getWays()) {
                final Polygon pWay = osmWay.getDrawablePolygon(myZTFMatrix);

                for (int i = 0; i < pWay.npoints; i++) {
                    polygon.addPoint(pWay.xpoints[i], pWay.ypoints[i]);
                }
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

    public List<OsmWay> getWays() {
        if (ways == null) {
            ways = new ArrayList<>();
        }

        return ways;
    }
}
