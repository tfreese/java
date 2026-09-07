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
public class PolygonBounds extends Bounds {
    /**
     * We use this path for inclusion-tests.
     */
    private final GeneralPath myPolygonPath = new GeneralPath(Path2D.WIND_EVEN_ODD);

    public void addPoint(final double lat, final double lon) {
        if (myPolygonPath.getCurrentPoint() == null) {
            myPolygonPath.moveTo(lat, lon);
        }
        else {
            myPolygonPath.lineTo(lat, lon);
        }
    }

    public void addPoint(final LatLon point) {
        addPoint(point.lat(), point.lon());
    }

    @Override
    public LatLon center() {
        return getCenter();
    }

    @Override
    public boolean contains(final double aLatitude, final double longitude) {
        myPolygonPath.closePath();

        return myPolygonPath.contains(aLatitude, longitude);
    }

    @Override
    public LatLon getCenter() {
        myPolygonPath.closePath();
        final Rectangle2D bounds2D = myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getCenterX(), bounds2D.getCenterY());
    }

    @Override
    public LatLon getMax() {
        myPolygonPath.closePath();
        final Rectangle2D bounds2D = myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getMaxX(), bounds2D.getMaxY());
    }

    @Override
    public LatLon getMin() {
        myPolygonPath.closePath();
        final Rectangle2D bounds2D = myPolygonPath.getBounds2D();

        return new LatLon(bounds2D.getMinX(), bounds2D.getMinY());
    }

    @Override
    public double getSize() {
        myPolygonPath.closePath();
        final Rectangle2D bounds2D = myPolygonPath.getBounds2D();

        return Math.max(bounds2D.getWidth(), bounds2D.getHeight());
    }

    @Override
    public String toString() {
        return "PolygonBounds@" + hashCode();
    }
}
