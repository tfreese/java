// Created: 03.12.2020
package de.freese.jconky;

import de.freese.jconky.system.LinuxSystemMonitor;
import de.freese.jconky.system.SystemMonitor;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Thomas Freese
 */
public final class Settings
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SettingsHolder
    {
        private static final Settings INSTANCE = new Settings();

        private SettingsHolder()
        {
            super();
        }
    }

    public static Settings getInstance()
    {
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

    private Settings()
    {
        super();

        this.systemMonitor = new LinuxSystemMonitor();

        this.alpha = 0.65D;
        this.colorText = Color.LIGHTGRAY.brighter();
        this.colorTitle = Color.web("#CC9900");
        this.colorValue = Color.web("#009BF9");
        this.colorGradientStart = Color.GREEN;
        this.colorGradientStop = Color.RED;

        this.fontName = "DejaVu Sans Mono";
        this.fontSize = 12D;
        this.font = Font.font(this.fontName, this.fontSize); // new Font(getFontName(), getFontSize());

        this.marginOuter = new Insets(5D, 5D, 5D, 5D);
        this.marginInner = new Insets(2.5D, 2.5D, 2.5D, 2.5D);
    }

    public double getAlpha()
    {
        return this.alpha;
    }

    public Color getColorGradientStart()
    {
        return this.colorGradientStart;
    }

    public Color getColorGradientStop()
    {
        return this.colorGradientStop;
    }

    public Color getColorText()
    {
        return this.colorText;
    }

    public Color getColorTitle()
    {
        return this.colorTitle;
    }

    public Color getColorValue()
    {
        return this.colorValue;
    }

    public Font getFont()
    {
        return this.font;
    }

    public String getFontName()
    {
        return this.fontName;
    }

    public double getFontSize()
    {
        return this.fontSize;
    }

    /**
     * Innerer Rand.
     */
    public Insets getMarginInner()
    {
        return this.marginInner;
    }

    /**
     * Äusserer Rand.
     */
    public Insets getMarginOuter()
    {
        return this.marginOuter;
    }

    public SystemMonitor getSystemMonitor()
    {
        return this.systemMonitor;
    }

    public boolean isDebug()
    {
        return false;
    }
}
