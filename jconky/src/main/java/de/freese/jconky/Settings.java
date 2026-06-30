// Created: 03.12.2020
package de.freese.jconky;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import de.freese.jconky.system.LinuxSystemMonitor;
import de.freese.jconky.system.SystemMonitor;

/**
 * @author Thomas Freese
 */
public final class Settings {
    private static final boolean DEBUG = false;

    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SettingsHolder {
        private static final Settings INSTANCE = new Settings();

        private SettingsHolder() {
            super();
        }
    }

    public static Settings getInstance() {
        return SettingsHolder.INSTANCE;
    }

    private final double alpha;
    private final Color colorGradientStart;
    private final Color colorGradientStop;
    private final Color colorText;
    private final Color colorTitle;
    private final Color colorValue;
    private final Font font;
    private final String fontName;
    private final double fontSize;
    private final Insets marginInner;
    private final Insets marginOuter;
    private final SystemMonitor systemMonitor;

    private Settings() {
        super();

        systemMonitor = new LinuxSystemMonitor();

        alpha = 0.65D;
        colorText = Color.LIGHTGRAY.brighter();
        colorTitle = Color.web("#CC9900");
        colorValue = Color.web("#009BF9");
        colorGradientStart = Color.GREEN;
        colorGradientStop = Color.RED;

        fontName = "DejaVu Sans Mono";
        fontSize = 12D;
        font = Font.font(fontName, fontSize); // new Font(getFontName(), getFontSize());

        marginOuter = new Insets(5D, 5D, 5D, 5D);
        marginInner = new Insets(2.5D, 2.5D, 2.5D, 2.5D);
    }

    public double getAlpha() {
        return alpha;
    }

    public Color getColorGradientStart() {
        return colorGradientStart;
    }

    public Color getColorGradientStop() {
        return colorGradientStop;
    }

    public Color getColorText() {
        return colorText;
    }

    public Color getColorTitle() {
        return colorTitle;
    }

    public Color getColorValue() {
        return colorValue;
    }

    public Font getFont() {
        return font;
    }

    public String getFontName() {
        return fontName;
    }

    public double getFontSize() {
        return fontSize;
    }

    /**
     * Innerer Rand.
     */
    public Insets getMarginInner() {
        return marginInner;
    }

    /**
     * Äusserer Rand.
     */
    public Insets getMarginOuter() {
        return marginOuter;
    }

    public SystemMonitor getSystemMonitor() {
        return systemMonitor;
    }

    public boolean isDebug() {
        return DEBUG;
    }
}
