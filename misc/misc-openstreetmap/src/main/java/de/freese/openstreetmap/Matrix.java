package de.freese.openstreetmap;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Enthält eine dreidimensionale Matrix für die Verrechnung von Geodaten.<br>
 * Da die Tiefen-/Höheninformation für zweidimensionale Darstellungen irrelevant ist, wird diese neutralisiert.
 *
 * @author Thomas Freese
 */
public class Matrix {
    /**
     * Liefert den Faktor, der benötigt wird, um das world-Rechteck in das win-Rechteck zu skalieren (einzupassen) bezogen auf die Breite der x-Achse.
     *
     * @param world Das Rechteck in Weltkoordinaten
     * @param win Das Rechteck in Bildschirmkoordinaten
     *
     * @return Der Skalierungsfaktor
     */
    public static double getZoomFactorX(final Rectangle world, final Rectangle win) {
        return win.getWidth() / world.getWidth();
    }

    /**
     * Liefert den Faktor, der benötigt wird, um das world-Rechteck in das win-Rechteck zu skalieren (einzupassen) bezogen auf die Höhe der y-Achse.
     *
     * @param world Das Rechteck in Weltkoordinaten
     * @param win Das Rechteck in Bildschirmkoordinaten
     *
     * @return Der Skalierungsfaktor
     */
    public static double getZoomFactorY(final Rectangle world, final Rectangle win) {
        return win.getHeight() / world.getHeight();
    }

    /**
     * Liefert eine Spiegelungsmatrix (x-Achse).
     *
     * @return Die Spiegelungsmatrix
     */
    public static Matrix mirrorX() {
        final Matrix myMat = new Matrix();
        myMat.m11 = 1;
        myMat.m12 = 0;
        myMat.m13 = 0;

        myMat.m21 = 0;
        myMat.m22 = -1;
        myMat.m23 = 0;

        myMat.m31 = 0;
        myMat.m32 = 0;
        myMat.m33 = 1;

        return myMat;
    }

    /**
     * Liefert eine Spiegelungsmatrix (y-Achse).
     *
     * @return Die Spiegelungsmatrix
     */
    public static Matrix mirrorY() {
        final Matrix myMat = new Matrix();
        myMat.m11 = -1;
        myMat.m12 = 0;
        myMat.m13 = 0;

        myMat.m21 = 0;
        myMat.m22 = 1;
        myMat.m23 = 0;

        myMat.m31 = 0;
        myMat.m32 = 0;
        myMat.m33 = 1;

        return myMat;
    }

    /**
     * Liefert eine Rotationsmatrix.
     *
     * @param alpha Der Winkel (in rad), um den rotiert werden soll
     *
     * @return Die Rotationsmatrix
     */
    public static Matrix rotate(final double alpha) {
        final Matrix myMat = new Matrix();
        myMat.m11 = Math.cos(alpha);
        myMat.m12 = -1 * Math.sin(alpha);
        myMat.m13 = 0;

        myMat.m21 = Math.sin(alpha);
        myMat.m22 = Math.cos(alpha);
        myMat.m23 = 0;

        myMat.m31 = 0;
        myMat.m32 = 0;
        myMat.m33 = 1;

        return myMat;
    }

    /**
     * Liefert eine Skalierungsmatrix.
     *
     * @param scaleVal Der Skalierungswert der Matrix
     *
     * @return Die Skalierungsmatrix
     */
    public static Matrix scale(final double scaleVal) {
        final Matrix myMat = new Matrix();
        myMat.m11 = scaleVal;
        myMat.m12 = 0;
        myMat.m13 = 0;

        myMat.m21 = 0;
        myMat.m22 = scaleVal;
        myMat.m23 = 0;

        myMat.m31 = 0;
        myMat.m32 = 0;
        myMat.m33 = 1;

        return myMat;
    }

    /**
     * Liefert eine Translationsmatrix.
     *
     * @param x Der Translationswert der Matrix in X-Richtung
     * @param y Der Translationswert der Matrix in Y-Richtung
     *
     * @return Die Translationsmatrix
     */
    public static Matrix translate(final double x, final double y) {
        final Matrix myMat = new Matrix();
        myMat.m11 = 1;
        myMat.m12 = 0;
        myMat.m13 = x;

        myMat.m21 = 0;
        myMat.m22 = 1;
        myMat.m23 = y;

        myMat.m31 = 0;
        myMat.m32 = 0;
        myMat.m33 = 1;

        return myMat;
    }

    /**
     * Liefert eine Translationsmatrix.
     */
    public static Matrix translate(final Point point) {
        return translate(point.getX(), point.getY());
    }

    /**
     * Liefert eine Matrix, die eine vorhandene Transformationsmatrix erweitert, um an einem bestimmten Punkt um einen bestimmten Faktor in die Karte hinein-
     * bzw. heraus zu zoomen.
     *
     * @param old Die zu erweiternde Transformationsmatrix
     * @param zoomPt Der Punkt an dem gezoomt werden soll
     * @param zoomScale Der Zoom-Faktor um den gezoomt werden soll
     *
     * @return Die neue Transformationsmatrix
     */
    public static Matrix zoomPoint(final Matrix old, final Point zoomPt, final double zoomScale) {
        // create translatermatrix (point to 0/0)
        final Matrix transform1 = translate(-zoomPt.getX(), -zoomPt.getY());

        // create scalematrix
        final Matrix scale = Matrix.scale(zoomScale);

        // create translate matrix 2 (0/0 to old point which remains unchanged)
        final Matrix transform2 = translate(zoomPt);

        // mul from back
        return transform2.multiply(scale.multiply(transform1.multiply(old)));
    }

    /**
     * Liefert eine Matrix, die alle notwendigen Transformationen beinhaltet (Translation, Skalierung, Spiegelung und Translation), um ein world-Rechteck in ein
     * win-Rechteck abzubilden.
     *
     * @param world Das Rechteck in Weltkoordinaten
     * @param win Das Rechteck in Bildschirmkoordinaten
     *
     * @return Die Transformationsmatrix
     */
    public static Matrix zoomToFit(final Rectangle world, final Rectangle win) {
        // 1 - move center to 0
        // double alpha = 0 - _world.getCenterX();
        final Matrix translateStep1 = translate(0 - world.getCenterX(), 0 - world.getCenterY());

        // 2 - Scale
        Matrix scaleBy = null;

        if (getZoomFactorX(world, win) < getZoomFactorY(world, win)) {
            scaleBy = scale(getZoomFactorX(world, win));
        }
        else {
            scaleBy = scale(getZoomFactorY(world, win));
        }

        // 3 - mirror by X
        // X-Achse der Bildschirmkoordinaten läuft realen Koordinaten entgegen.
        final Matrix mirrorByX = mirrorX();

        // 4 - move to recenter
        final Matrix translateStep2 = translate(win.getCenterX(), win.getCenterY());

        return translateStep2.multiply(mirrorByX.multiply(scaleBy.multiply(translateStep1)));
    }

    private double m11;
    private double m12;
    private double m13;
    private double m21;
    private double m22;
    private double m23;
    private double m31;
    private double m32;
    private double m33;

    public Matrix() {
        super();

        m11 = 0.0D;
        m12 = 0.0D;
        m13 = 0.0D;

        m21 = 0.0D;
        m22 = 0.0D;
        m23 = 0.0D;

        m31 = 0.0D;
        m32 = 0.0D;
        m33 = 0.0D;
    }

    /**
     * Liefert die Invers-Matrix der Transformationsmatrix.
     */
    public Matrix invers() {
        final double myDet = ((m11 * m22 * m33) + (m12 * m23 * m31) + (m13 * m21 * m32))
                - (m11 * m23 * m32) - (m12 * m21 * m33) - (m13 * m22 * m31);

        final Matrix retval = new Matrix();
        retval.m11 = (m22 * m33) - (m32 * m23);
        retval.m12 = (m13 * m32) - (m33 * m12);
        retval.m13 = (m12 * m23) - (m13 * m22);

        retval.m21 = (m23 * m31) - (m33 * m21);
        retval.m22 = (m11 * m33) - (m31 * m13);
        retval.m23 = (m13 * m21) - (m23 * m11);

        retval.m31 = (m21 * m32) - (m31 * m22);
        retval.m32 = (m12 * m31) - (m32 * m11);
        retval.m33 = (m11 * m22) - (m21 * m12);

        retval.m11 *= 1 / myDet;
        retval.m12 *= 1 / myDet;
        retval.m13 *= 1 / myDet;

        retval.m21 *= 1 / myDet;
        retval.m22 *= 1 / myDet;
        retval.m23 *= 1 / myDet;

        retval.m31 *= 1 / myDet;
        retval.m32 *= 1 / myDet;
        retval.m33 *= 1 / myDet;

        return retval;
    }

    /**
     * Liefert eine Matrix, die das Ergebnis einer Matrizenmultiplikation zwischen dieser und der übergebenen Matrix ist.
     *
     * @param other Die Matrix mit der Multipliziert werden soll
     *
     * @return Die Ergebnismatrix der Multiplikation
     */
    public Matrix multiply(final Matrix other) {
        final Matrix retval = new Matrix();
        retval.m11 = (m11 * other.m11) + (m12 * other.m21) + (m13 * other.m31);
        retval.m12 = (m11 * other.m12) + (m12 * other.m22) + (m13 * other.m32);
        retval.m13 = (m11 * other.m13) + (m12 * other.m23) + (m13 * other.m33);

        retval.m21 = (m21 * other.m11) + (m22 * other.m21) + (m23 * other.m31);
        retval.m22 = (m21 * other.m12) + (m22 * other.m22) + (m23 * other.m32);
        retval.m23 = (m21 * other.m13) + (m22 * other.m23) + (m23 * other.m33);

        retval.m31 = (m31 * other.m11) + (m32 * other.m21) + (m33 * other.m31);
        retval.m32 = (m31 * other.m12) + (m32 * other.m22) + (m33 * other.m32);
        retval.m33 = (m31 * other.m13) + (m32 * other.m23) + (m33 * other.m33);

        return retval;
    }

    /**
     * Multipliziert einen Punkt mit der Matrix und liefert das Ergebnis der Multiplikation zurück.
     *
     * @param pt Der Punkt, der mit der Matrix multipliziert werden soll
     *
     * @return Ein neuer Punkt, der das Ergebnis der Multiplikation repräsentiert
     */
    public Point multiply(final Point pt) {
        final Point retval = new Point();
        retval.x = (int) ((pt.x * m11) + (pt.y * m12) + m13);
        retval.y = (int) ((pt.x * m21) + (pt.y * m22) + m23);

        return retval;
    }

    /**
     * Multipliziert ein Polygon mit der Matrix und liefert das Ergebnis der Multiplikation zurück.
     *
     * @param polygon Das Polygon, das mit der Matrix multipliziert werden soll
     *
     * @return Ein neues Polygon, das das Ergebnis der Multiplikation repräsentiert
     */
    public Polygon multiply(final Polygon polygon) {
        final Polygon retval = new Polygon();

        for (int i = 0; i < polygon.npoints; i++) {
            final Point oldpoint = new Point();
            oldpoint.x = polygon.xpoints[i];
            oldpoint.y = polygon.ypoints[i];

            final Point newpoint = multiply(oldpoint);
            retval.addPoint(newpoint.x, newpoint.y);
        }

        return retval;
    }

    /**
     * Multipliziert ein Rechteck mit der Matrix und liefert das Ergebnis der Multiplikation zurück.
     *
     * @param rect Das Rechteck, das mit der Matrix multipliziert werden soll
     *
     * @return Ein neues Rechteck, das das Ergebnis der Multiplikation repräsentiert
     */
    public Rectangle multiply(final Rectangle rect) {
        Point toppoint = new Point(rect.x, rect.y);
        Point btpoint = new Point(rect.x + rect.width, rect.y + rect.height);

        toppoint = multiply(toppoint);
        btpoint = multiply(btpoint);

        final Rectangle retVal = new Rectangle(toppoint);
        retVal.add(btpoint);

        return retVal;
    }

    /**
     * Liefert eine String-Repräsentation der Matrix
     *
     * @return Ein String mit dem Inhalt der Matrix
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("|").append(m11).append(";").append("|").append(m12).append(";").append("|").append(m13).append("|").append(System.lineSeparator());
        sb.append("|").append(m21).append(";").append("|").append(m22).append(";").append("|").append(m23).append("|").append(System.lineSeparator());
        sb.append("|").append(m31).append(";").append("|").append(m32).append(";").append("|").append(m33).append("|").append(System.lineSeparator());

        return sb.toString();
    }
}
