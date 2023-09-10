// Created: 29.11.2018
package de.freese.sonstiges.svg;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * @author Thomas Freese
 */
public class BufferedImageTranscoder extends ImageTranscoder {
    private BufferedImage image;

    @Override
    public BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }

    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) throws TranscoderException {
        this.image = img;
    }
}
