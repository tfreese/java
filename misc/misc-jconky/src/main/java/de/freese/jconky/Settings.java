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
        /**
         *
         */
        private static final Settings INSTANCE = new Settings();

        /**
         * Erstellt ein neues {@link SettingsHolder} Object.
         */
        private SettingsHolder()
        {
            super();
        }
    }

    /**
     * @return {@link Settings}
     */
    public static Settings getInstance()
    {
        return SettingsHolder.INSTANCE;
    }

    /**
     *
     */
    private final double alpha;
    /**
     *
     */
    private final Color colorGradientStart;
    /**
     *
     */
    private final Color colorGradientStop;
    /**
     *
     */
    private final Color colorText;
    /**
     *
     */
    private final Color colorTitle;
    /**
     *
     */
    private final Color colorValue;
    /**
     *
     */
    private final Font font;
    /**
     *
     */
    private final String fontName;
    /**
     *
     */
    private final double fontSize;
    /**
     *
     */
    private final Insets marginInner;
    /**
     *
     */
    private final Insets marginOuter;
    /**
     *
     */
    private final SystemMonitor systemMonitor;

    /**
     * Erstellt ein neues {@link Settings} Object.
     */
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

    /**
     * @return double
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * @return {@link Color}
     */
    public Color getColorGradientStart()
    {
        return this.colorGradientStart;
    }

    /**
     * @return {@link Color}
     */
    public Color getColorGradientStop()
    {
        return this.colorGradientStop;
    }

    /**
     * @return {@link Color}
     */
    public Color getColorText()
    {
        return this.colorText;
    }

    /**
     * @return {@link Color}
     */
    public Color getColorTitle()
    {
        return this.colorTitle;
    }

    /**
     * @return {@link Color}
     */
    public Color getColorValue()
    {
        return this.colorValue;
    }

    /**
     * @return {@link Font}
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * @return int
     */
    public String getFontName()
    {
        return this.fontName;
    }

    /**
     * @return double
     */
    public double getFontSize()
    {
        return this.fontSize;
    }

    /**
     * Innerer Rand.
     *
     * @return {@link Insets}
     */
    public Insets getMarginInner()
    {
        return this.marginInner;
    }

    /**
     * Äusserer Rand.
     *
     * @return {@link Insets}
     */
    public Insets getMarginOuter()
    {
        return this.marginOuter;
    }

    /**
     * @return {@link SystemMonitor}
     */
    public SystemMonitor getSystemMonitor()
    {
        return this.systemMonitor;
    }

    /**
     * @return boolean
     */
    public boolean isDebug()
    {
        return false;
    }
}
