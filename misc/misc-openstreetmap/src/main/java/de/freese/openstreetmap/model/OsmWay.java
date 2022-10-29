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
public class OsmWay extends AbstractOsmEntity
{
    /**
     *
     */
    public List<OsmNode> nodes;
    /**
     *
     */
    private Rectangle bounds;
    /**
     *
     */
    private Polygon polygon;

    /**
     * @return {@link Rectangle}
     */
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
        }

        return this.bounds;
    }

    /**
     * @param myZTFMatrix {@link Matrix}
     *
     * @return {@link Polygon}
     */
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
        }

        return myZTFMatrix.multiply(this.polygon);
    }

    /**
     * @return {@link List}<OsmNode>
     */
    public List<OsmNode> getNodes()
    {
        if (this.nodes == null)
        {
            this.nodes = new ArrayList<>();
        }

        return this.nodes;
    }
}
