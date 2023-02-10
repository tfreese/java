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
 * BasisModel für Simulationen mit Zellen als Pixeln.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRasterSimulation extends AbstractSimulation {
    private final Image image;

    private final MemoryImageSource imageSource;
    /**
     * Pixel-Backend für {@link MemoryImageSource} und {@link Image}.
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

    /**
     * @see de.freese.simulationen.model.Simulation#getImage()
     */
    @Override
    public Image getImage() {
        return this.image;
    }

    /**
     * @see de.freese.simulationen.model.Simulation#reset()
     */
    @Override
    public void reset() {
        // @formatter:off
        IntStream.range(0, getHeight())
            .parallel()
            .forEach(y ->
            {
                for (int x = 0; x < getWidth(); x++)
                {
                    reset(x, y);
                }
            })
            ;
        // @formatter:on

        fireCompleted();
    }

    /**
     * Einmaliges befüllen des Rasters.
     */
    protected final void fillRaster(final Supplier<Cell> cellSupplier) {
        Set<Cell> set = Collections.synchronizedSet(new HashSet<>());

        // @formatter:off
        IntStream.range(0, getHeight())
            .parallel()
            .forEach(y ->
            {
                for (int x = 0; x < getWidth(); x++)
                {
                    Cell cell = cellSupplier.get();

                    if(cell instanceof AbstractCell c)
                    {
                        c.setXY(x, y);
                    }

                    this.raster[x][y] = cell;

                    set.add(cell);
                }
            })
            ;
        // @formatter:on

        this.cells = Set.copyOf(set);
    }

    protected Cell getCell(final int x, final int y) {
        return this.raster[x][y];
    }

    /**
     * Liefert einen parallelen {@link Stream} für die Zellen.
     */
    protected Stream<Cell> getCellStream() {
        // Der Stream vom Raster bildet Wellen-Fronten, da immer von oben links angefangen wird zu rechnen.
        // return Stream.of(this.raster).parallel().flatMap(Stream::of).parallel();

        return this.cells.stream().parallel();
    }

    /**
     * Reset einer Zelle des Rasters.
     */
    protected void reset(final int x, final int y) {
        // Empty
    }

    /**
     * Ändert die Pixel-Farbe an den Koordinaten.
     */
    protected void setCellColor(final int x, final int y, final Color color) {
        this.pixelsRGB[x + (y * getWidth())] = color.getRGB();
    }

    /**
     * @see de.freese.simulationen.model.AbstractSimulation#updateImage()
     */
    @Override
    protected void updateImage() {
        this.imageSource.newPixels();
        // this.imageSource.newPixels(0, 0, getWidth(), getHeight());
    }
}
