// Created: 23.12.2020
package de.freese.jconky.painter;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.util.JConkyUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author Thomas Freese
 */
public class MusicMonitorPainter extends AbstractMonitorPainter
{
    /**
     *
     */
    private Image image;

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        MusicInfo musicInfo = getContext().getMusicInfo();

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;
        paintTitle(gc, "Music", x, y, width);

        y += fontSize * 1.25D;
        paintText(gc, "Artist: " + Objects.toString(musicInfo.getArtist(), ""), x, y);

        y += fontSize * 1.25D;
        paintText(gc, "Album : " + Objects.toString(musicInfo.getAlbum(), ""), x, y);

        y += fontSize * 1.25D;
        paintText(gc, "Title : " + Objects.toString(musicInfo.getTitle(), ""), x, y);

        y += fontSize * 1.25D;
        String position = JConkyUtils.toClockString(musicInfo.getPosition(), "%d:%02d:%02d", "%d:%02d");
        String length = JConkyUtils.toClockString(musicInfo.getLength(), "%d:%02d:%02d", "%d:%02d");
        String text = String.format("%s / %s, %.2f %%, %d KB/s", position, length, musicInfo.getProgress() * 100D, musicInfo.getBitRate());
        paintText(gc, "Time  : " + text, x, y);

        // updateImage(musicInfo.getImageUri());
        //
        // y += 5D;
        // double imageWidth = width - getSettings().getMarginInner().getLeft() - getSettings().getMarginInner().getRight();
        // double imageHeight = imageWidth;
        //
        // if (musicInfo.getImageUri() != null)
        // {
        // double globalAlpha = gc.getGlobalAlpha();
        // gc.setGlobalAlpha(getSettings().getAlpha());
        // gc.drawImage(this.image, x, y, imageWidth, imageHeight);
        // gc.setGlobalAlpha(globalAlpha);
        // }
        //
        // y += imageHeight;

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    /**
     * Bild nur laden, wenn nicht vorhanden oder URL sich verändert hat.
     *
     * @param uri {@link URI}
     */
    void updateImage(final URI uri)
    {
        if (uri == null)
        {
            this.image = null;
            return;
        }

        try
        {
            URL url = uri.toURL();
            String urlString = url.toString();
            getLogger().debug("URL: {}", url);

            if ((this.image == null) || !this.image.getUrl().equals(urlString))
            {
                this.image = new Image(urlString);

                getLogger().debug("Image-URL: {}", this.image.getUrl());
            }
        }
        catch (Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
