package de.freese.openstreetmap.model.coordinates;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * (c) 2007 by <a href="http://Wolschon.biz>Wolschon Softwaredesign und Beratung</a>.<br/>
 * Project: libosm<br/>
 * PolygonBounds.java<br/>
 * created: 18.11.2007 21:26:33 <br/>
 * <br/>
 * <br/>
 * These special bounds are denoted by a polygon instead of a simple bounding-box.
 *
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 */
public class PolygonBounds extends Bounds
{
    /**
     * We use this path for inclusion-tests.
     */
    private final GeneralPath myPolygonPath = new GeneralPath(Path2D.WIND_EVEN_ODD);

    /**
     * Add a point to this polygon.
     *
     * @param lat the latitude
     * @param lon the longitude
     */
    public void addPoint(final double lat, final double lon)
    {
        if (this.myPolygonPath.getCurrentPoint() == null)
        {
            this.myPolygonPath.moveTo(lat, lon);
        }
        else
        {
            this.myPolygonPath.lineTo(lat, lon);
        }
    }

    /**
     * Add a point to this polygon.
     *
     * @param point the point to add.
     */
    public void addPoint(final LatLon point)
    {
        addPoint(point.lat(), point.lon());
    }

    /**
     * @return the center of these bounds.
     */
    @Override
    public LatLon center()
    {
        return getCenter();
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#contains(double, double)
     */
    @Override
    public boolean contains(final double aLatitude, final double aLongitude)
    {
        this.myPolygonPath.closePath();

        return this.myPolygonPath.contains(aLatitude, aLongitude);
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#getCenter()
     */
    @Override
    public LatLon getCenter()
    {
        this.myPolygonPath.closePath();
        Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getCenterX(), bounds2D.getCenterY());
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#getMax()
     */
    @Override
    public LatLon getMax()
    {
        this.myPolygonPath.closePath();
        Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getMaxX(), bounds2D.getMaxY());
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#getMin()
     */
    @Override
    public LatLon getMin()
    {
        this.myPolygonPath.closePath();
        Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getMinX(), bounds2D.getMinY());
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#getSize()
     */
    @Override
    public double getSize()
    {
        this.myPolygonPath.closePath();
        Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

        return Math.max(bounds2D.getWidth(), bounds2D.getHeight());
    }

    /**
     * @see de.freese.openstreetmap.model.coordinates.Bounds#toString()
     */
    @Override
    public String toString()
    {
        return "PolygonBounds@" + hashCode();
    }
}
