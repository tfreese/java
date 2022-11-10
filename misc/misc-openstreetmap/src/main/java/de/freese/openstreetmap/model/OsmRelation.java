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
public class OsmRelation extends AbstractOsmEntity
{
    public List<OsmNode> nodes;

    public List<OsmWay> ways;

    private Rectangle bounds;

    private Polygon polygon;

    public Rectangle getBounds()
    {
        if (this.bounds == null)
        {
            for (OsmNode osmNode : getNodes())
            {
                double x = Mercator.mercX(osmNode.getLongitude());
                double y = Mercator.mercY(osmNode.getLatitude());

                if (this.bounds == null)
                {
                    this.bounds = new Rectangle();
                    this.bounds.x = (int) x;
                    this.bounds.y = (int) y;
                }

                this.bounds.add(x, y);
            }

            if (this.bounds == null)
            {
                this.bounds = new Rectangle(0, 0);
            }

            for (OsmWay osmWay : getWays())
            {
                this.bounds = this.bounds.union(osmWay.getBounds());
            }
        }

        return this.bounds;
    }

    public Polygon getDrawablePolygon(final Matrix myZTFMatrix)
    {
        if (this.polygon == null)
        {
            this.polygon = new Polygon();

            for (OsmNode osmNode : getNodes())
            {
                double x = Mercator.mercX(osmNode.getLongitude());
                double y = Mercator.mercY(osmNode.getLatitude());
                this.polygon.addPoint((int) x, (int) y);
            }

            for (OsmWay osmWay : getWays())
            {
                Polygon pWay = osmWay.getDrawablePolygon(myZTFMatrix);

                for (int i = 0; i < pWay.npoints; i++)
                {
                    this.polygon.addPoint(pWay.xpoints[i], pWay.ypoints[i]);
                }
            }
        }

        return myZTFMatrix.multiply(this.polygon);
    }

    public List<OsmNode> getNodes()
    {
        if (this.nodes == null)
        {
            this.nodes = new ArrayList<>();
        }

        return this.nodes;
    }

    public List<OsmWay> getWays()
    {
        if (this.ways == null)
        {
            this.ways = new ArrayList<>();
        }

        return this.ways;
    }
}
