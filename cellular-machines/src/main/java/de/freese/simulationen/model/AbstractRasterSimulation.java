// Created: 18.09.2009
package de.freese.simulationen.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * BaseModel for Simulations with Cells as Pixels.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRasterSimulation extends AbstractSimulation {

    private final Image image;
    private final MemoryImageSource imageSource;
    /**
     * Pixel-Backend for {@link MemoryImageSource} and {@link Image}.
     */
    private final int[] pixelsRGB;
    private final Cell[][] raster;

    private Set<Cell> cells;

    protected AbstractRasterSimulation(final int width, final int height) {
        super(width, height);

        this.pixelsRGB = new int[width * height];
        this.raster = new Cell[width][height];

        // Arrays.fill(this.pixelsRGB, getNullCellColor().getRGB());
        // Arrays.parallelSetAll(this.pixelsRGB, i -> getNullCellColor().getRGB());

        this.imageSource = new MemoryImageSource(width, height, this.pixelsRGB, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);

        this.image = Toolkit.getDefaultToolkit().createImage(this.imageSource);
        // java.awt.Component.createImage(this.imageSource);
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public void reset() {
        IntStream.range(0, getHeight())
                .parallel()
                .forEach(y -> {
                    for (int x = 0; x < getWidth(); x++) {
                        reset(x, y);
                    }
                })
        ;

        fireCompleted();
    }

    protected final void fillRaster(final Supplier<Cell> cellSupplier) {
        final Set<Cell> set = Collections.synchronizedSet(new HashSet<>());

        IntStream.range(0, getHeight())
                .parallel()
                .forEach(y -> {
                    for (int x = 0; x < getWidth(); x++) {
                        final Cell cell = cellSupplier.get();

                        if (cell instanceof AbstractCell c) {
                            c.setXY(x, y);
                        }

                        this.raster[x][y] = cell;

                        set.add(cell);
                    }
                })
        ;

        this.cells = Set.copyOf(set);
    }

    protected Cell getCell(final int x, final int y) {
        return this.raster[x][y];
    }

    protected Stream<Cell> getCellStream() {
        // The Raster-Stream creates wave fronts, because the calculating starts in the upper left corner.
        // return Stream.of(this.raster).flatMap(Stream::of);

        return this.cells.stream().parallel();
    }

    protected void reset(final int x, final int y) {
        // Empty
    }

    protected void setCellColor(final int x, final int y, final Color color) {
        this.pixelsRGB[x + (y * getWidth())] = color.getRGB();
    }

    @Override
    protected void updateImage() {
        this.imageSource.newPixels();
        // this.imageSource.newPixels(0, 0, getWidth(), getHeight());
    }
}
