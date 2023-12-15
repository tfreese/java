// Created: 26.09.2016
package de.freese.simulationen.balls;

import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * Bewegender Ball.
 *
 * @author Thomas Freese
 */
public class Ball {
    /**
     * Gravitationskonstante [m/s²]
     */
    private static final double GRAVITATION = 9.80665;
    /**
     * [%]
     */
    private final double daempfung;
    /**
     * [m]
     */
    private final double durchmesser;
    /**
     * Horizontale Größe des Koordinatensystems [m].
     */
    private final IntSupplier maxX;
    /**
     * Vertikale Größe des Koordinatensystems [m].
     */
    private final IntSupplier maxY;
    /**
     * [m]
     */
    private final double radius;
    private boolean finished;
    /**
     * Horizontale Geschwindigkeit [m/s].
     */
    private double vx;
    /**
     * Vertikale Geschwindigkeit [m/s].
     */
    private double vy;
    /**
     * Aktuelle X-Koordinate [m].
     */
    private double x;
    /**
     * Aktuelle Y-Koordinate [m].
     */
    private double y;

    /**
     * @param maxX Horizontale Größe des Koordinatensystems [m].
     * @param maxY Vertikale Größe des Koordinatensystems [m].
     * @param x Aktuelle X-Koordinate [m].
     * @param y Aktuelle Y-Koordinate [m].
     * @param vx Horizontale Geschwindigkeit [m/s].
     * @param vy Vertikale Geschwindigkeit [m/s].
     * @param durchmesser [m]
     * @param daempfung [%]
     */
    Ball(final int maxX, final int maxY, final double x, final double y, final double vx, final double vy, final double durchmesser, final double daempfung) {
        this(() -> maxX, () -> maxY, x, y, vx, vy, durchmesser, daempfung);
    }

    /**
     * @param maxX {@link IntSupplier} [m].
     * @param maxY {@link IntSupplier} [m].
     * @param x Aktuelle X-Koordinate [m].
     * @param y Aktuelle Y-Koordinate [m].
     * @param vx Horizontale Geschwindigkeit [m/s].
     * @param vy Vertikale Geschwindigkeit [m/s].
     * @param durchmesser [m]
     * @param daempfung [%]
     */
    Ball(final IntSupplier maxX, final IntSupplier maxY, final double x, final double y, final double vx, final double vy, final double durchmesser, final double daempfung) {
        super();

        this.maxX = maxX;
        this.maxY = maxY;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.durchmesser = durchmesser;
        this.radius = durchmesser / 2.0D;
        this.daempfung = daempfung;
    }

    /**
     * [m]
     */
    public double getDurchmesser() {
        return this.durchmesser;
    }

    /**
     * Breite des Koordinatensystems [m].
     */
    public int getMaxX() {
        return this.maxX.getAsInt();
    }

    /**
     * Höhe des Koordinatensystems [m].
     */
    public int getMaxY() {
        return this.maxY.getAsInt();
    }

    /**
     * [m]
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Aktuelle X-Koordinate [m].
     */
    public double getX() {
        return this.x;
    }

    /**
     * Aktuelle Y-Koordinate [m].
     */
    public double getY() {
        return this.y;
    }

    /**
     * Liefert true, wenn der Ball zum stillstand gekommen ist.
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Berechnet die neue Position (Flugbahn) des Balls nach der angegebenen Zeitdifferenz.
     *
     * @param dt Zeit [ms]
     */
    public void move(final double dt) {
        if (this.finished) {
            return;
        }

        double deltaTime = dt / 1000.0D; // ms -> s
        double timeToX = 0.0D; // Flugzeit bis zum linken oder rechten Rand.
        double timeToY = 0.0D; // Flugzeit bis zum unteren oder oberen Rand.
        boolean touchX = false;
        boolean touchY = false;

        // Kollisionsabfrage mit dem Rand.
        if (this.vx > 0.0D) {
            // Ball fliegt nach rechts.
            timeToX = flytimeRechts();
            //
            if (timeToX < deltaTime) {
                // Ball würde vor dt rechten Rand berühren.
                touchX = true;
            }
        }
        else if (this.vx < 0.0D) {
            // Ball fliegt nach links.
            timeToX = flytimeLinks();
            //
            if (timeToX < deltaTime) {
                // Ball würde vor dt linken Rand berühren.
                touchX = true;
            }
        }

        if (this.vy < 0.0D) {
            // Ball fliegt nach unten.
            timeToY = flytimeUnten();

            if (timeToY < deltaTime) {
                // Ball würde vor dt unteren Rand berühren.
                touchY = true;
            }
        }
        else if (this.vy > 0.0D) {
            // Ball fliegt nach oben.
            timeToY = flytimeOben();
            //
            if (timeToY < deltaTime) {
                // Ball würde vor dt oberen Rand berühren.
                touchY = true;
            }
        }

        deltaTime = Math.min(deltaTime, Math.min(timeToX, timeToY));

        // s = v * t
        final double dx = this.vx * deltaTime;

        // v = a*t + vₒ
        this.vy = (-GRAVITATION * deltaTime) + this.vy;

        // s = sₒ + vₒt + ½gt²
        final double dy = (this.vy * deltaTime) + (0.5D * GRAVITATION * Math.pow(deltaTime, 2.0D));

        this.x += dx;
        this.y += dy;

        // Keine negativen Y-Koordinaten.
        if (this.y < this.radius) {
            this.y = this.radius;
        }

        // Bei Rand-Berührung Geschwindigkeit durch Dämpfung verringern.
        if (touchX) {
            this.vx *= (-1.0D + this.daempfung); // Umkehren
            this.vy *= (1.0D - (this.daempfung / 2.0D));
        }

        if (touchY) {
            this.vy *= (-1.0D + this.daempfung); // Umkehren
            this.vx *= (1.0D - (this.daempfung / 2.0D));
        }

        // Abbruch, wenn sich die kinetischen Energien kaum noch ändern.
        // Nutzung des Durchmessers durch Erfahrungswerte.
        if ((getEKin() <= (getDurchmesser() / 15.0D)) && (getEPot() <= (getDurchmesser() * 5.0D))) {
            this.finished = true;
        }
    }

    /**
     * Berechnet die neue Position (Flugbahn) des Balls nach der angegebenen Zeitdifferenz.<br>
     *
     * @param timeUnit {@link TimeUnit}
     */
    public void move(final long dt, final TimeUnit timeUnit) {
        move(timeUnit.toMillis(dt));
    }

    /**
     * Berechnung der Flugzeit zum linken Rand.<br>
     * Gleichmäßige Bewegung:<br>
     * s = v * t
     */
    private double flytimeLinks() {
        return Math.abs((this.x - this.radius) / this.vx);
    }

    /**
     * Berechnung der Flugzeit zum oberen Rand.<br>
     * Wurf nach oben:<br>
     * s = sₒ + vₒt + ½gt²<br>
     * v = vₒ + gt<br><br>
     * <br>
     * Quadratische Gleichung aufgelöst nach Mitternachtsformel:<br>
     * ax² + bx + c = 0<br>
     * x = (-b ± sqrt(b² - 4ac)) / 2a<br>
     * gt² + 2Vₒt + 2(sₒ - s) = 0<br>
     * t = (-2vₒ ± sqrt(2vₒ² - 4g * 2(sₒ - s))) / 2g<br>
     * <br>
     * Alternative: pq-Formel:<br>
     * x² + px + q = 0<br>
     * x = -p/2 ± sqrt((p/2)² - q)<br>
     * t² + (2Vₒ/g)*t + (2(sₒ - s)/g) = 0<br>
     * t = -(vₒ/g) ± sqrt((vₒ/g)² - (2(sₒ - s) / g))<br>
     */
    private double flytimeOben() {
        // double a = -GRAVITATION;
        // double b = 2.0D * this.vy;
        // double c = 2.0D * ((this.y + this.radius) - this.maxY);
        //
        // double t1 = (-b + Math.sqrt(Math.pow(b, 2.0D) - (4.0D * a * c))) / (2.0D * a);
        // double t2 = (-b - Math.sqrt(Math.pow(b, 2.0D) - (4.0D * a * c))) / (2.0D * a);

        final double p = this.vy / -GRAVITATION;
        final double q = (2.0D * ((this.y + this.radius) - this.maxY.getAsInt())) / -GRAVITATION;

        final double t1 = -p + Math.sqrt(Math.pow(p, 2.0D) - q);
        final double t2 = -p - Math.sqrt(Math.pow(p, 2.0D) - q);

        final double t = Math.min(Math.abs(t1), Math.abs(t2));

        // NaN, falls Geschwindigkeit zu gering um oberen Rand zu erreichen.
        return Double.isNaN(t) ? Double.MAX_VALUE : t;
    }

    /**
     * Berechnung der Flugzeit zum rechten Rand.<br>
     * Gleichmäßige Bewegung:<br>
     * s = v * t
     */
    private double flytimeRechts() {
        return Math.abs((this.maxX.getAsInt() - this.x - this.radius) / this.vx);
    }

    /**
     * Berechnung der Flugzeit zum unteren Rand.<br>
     * Wurf nach unten:<br>
     * s = sₒ + vₒt + ½gt²<br>
     * v = vₒ + gt<br>
     * <br>
     * Quadratische Gleichung aufgelöst nach Mitternachtsformel:<br>
     * ax² + bx + c = 0<br>
     * x = (-b ± sqrt(b² - 4ac)) / 2a<br>
     * gt² + 2Vₒt + 2(sₒ - s) = 0<br>
     * t = (-2vₒ ± sqrt(2vₒ² - 4g * 2(sₒ - s))) / 2g<br>
     * <br>
     * Alternative: pq-Formel:<br>
     * x² + px + q = 0<br>
     * x = -p/2 ± sqrt((p/2)² - q)<br>
     * t² + (2Vₒ/g)*t + (2(sₒ - s)/g) = 0<br>
     * t = -(vₒ/g) ± sqrt((vₒ/g)² - (2(sₒ - s) / g))<br>
     */
    private double flytimeUnten() {
        // final double a = GRAVITATION;
        // final double b = 2.0D * -this.vy;
        // final double c = 2.0D * (this.y - this.radius);
        //
        // final double t1 = (-b + Math.sqrt(Math.pow(b, 2.0D) - (4.0D * a * c))) / (2.0D * a);
        // final double t2 = (-b - Math.sqrt(Math.pow(b, 2.0D) - (4.0D * a * c))) / (2.0D * a);

        final double p = -this.vy / GRAVITATION;
        final double q = (2.0D * (this.y - this.radius)) / GRAVITATION;

        final double t1 = -p + Math.sqrt(Math.pow(p, 2.0D) - q);
        final double t2 = -p - Math.sqrt(Math.pow(p, 2.0D) - q);

        final double t = Math.min(Math.abs(t1), Math.abs(t2));

        return Double.isNaN(t) ? Double.MAX_VALUE : t;
    }

    /**
     * Liefert die kinetische Energie [J oder Nm].<br>
     * Wird für die Berechnung des horizontalen Stillstandes verwendet.<br>
     * E = ½ * m * v²<br>
     */
    private double getEKin() {
        return 0.5D * Math.pow(this.vx, 2.0D);
    }

    /**
     * Liefert die potenzielle Energie [J oder Nm].<br>
     * Wird für die Berechnung des vertikalen Stillstandes verwendet.<br>
     * E = m * g * h<br>
     */
    private double getEPot() {
        return GRAVITATION * getY();
    }
}
