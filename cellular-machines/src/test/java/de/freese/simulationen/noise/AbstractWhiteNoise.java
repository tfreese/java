// Created: 30 Nov. 2024
package de.freese.simulationen.noise;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntUnaryOperator;

/**
 * @author Thomas Freese
 */
abstract class AbstractWhiteNoise implements Runnable {
    private final transient Image image;
    private final transient MemoryImageSource imageSource;

    private final int[] pixelsRGB;
    private final Random random = new SecureRandom();

    protected AbstractWhiteNoise(final int pixelWidth, final int pixelHeight) {
        super();

        pixelsRGB = new int[pixelWidth * pixelHeight];
        imageSource = new MemoryImageSource(pixelWidth, pixelHeight, pixelsRGB, 0, pixelWidth);
        imageSource.setAnimated(true);
        imageSource.setFullBufferUpdates(false);

        // java.awt.Component.createImage(java.awt.image.ImageProducer)
        // image = createImage(imageSource);
        image = Toolkit.getDefaultToolkit().createImage(imageSource);
    }

    @Override
    public void run() {
        final IntUnaryOperator generator = i -> {
            if (random.nextBoolean()) {
                return Color.BLACK.getRGB();
            }

            return Color.WHITE.getRGB();
        };

        // for (int y = 0; y < getImageHeight(); y++) {
        // for (int x = 0; x < getImageWidth(); x++) {
        // pixelsRGB[x + (y * getImageWidth())] = generator.applyAsInt(0);
        // }
        // }
        //
        // for (int i = 0; i < pixelsRGB.length; i++) {
        // pixelsRGB[i] = generator.applyAsInt(0);
        // }
        Arrays.parallelSetAll(pixelsRGB, generator);

        imageSource.newPixels();
        repaintImage();
    }

    protected Image getImage() {
        return image;
    }

    protected abstract void repaintImage();
}
