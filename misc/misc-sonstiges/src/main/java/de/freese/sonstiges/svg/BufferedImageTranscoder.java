// Created: 29.11.2018
package de.freese.sonstiges.svg;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

/**
 * @author Thomas Freese
 * @see JPEGTranscoder
 * @see PNGTranscoder
 * @see TIFFTranscoder
 */
public class BufferedImageTranscoder extends ImageTranscoder
{
    /**
     *
     */
    private BufferedImage image;

    /**
     * @see org.apache.batik.transcoder.image.ImageTranscoder#createImage(int, int)
     */
    @Override
    public BufferedImage createImage(final int width, final int height)
    {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * @return {@link BufferedImage}
     */
    public BufferedImage getBufferedImage()
    {
        return this.image;
    }

    /**
     * @see org.apache.batik.transcoder.image.ImageTranscoder#writeImage(java.awt.image.BufferedImage, org.apache.batik.transcoder.TranscoderOutput)
     */
    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) throws TranscoderException
    {
        this.image = img;
    }
}
