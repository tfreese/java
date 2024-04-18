// Created: 09.03.2021
package de.freese.simulationen.wator;

import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.Cell;
import de.freese.simulationen.wator.WaTorCell.CellType;

/**
 * Model der WaTor-Simulation.<br>
 * <a href="http://de.academic.ru/dic.nsf/dewiki/1492493">WaTor-Simulation</a>
 *
 * @author Thomas Freese
 */
public class WaTorRasterSimulation extends AbstractRasterSimulation {
    private final LongAdder fishCounter = new LongAdder();
    private final LongAdder sharkCounter = new LongAdder();

    /**
     * Richtung der Berechnung.
     */
    private int direction;
    /**
     * Brut-Energie der Fische.
     */
    private int fishBreedEnergy = 5;
    /**
     * Start-Energie der Fische.
     */
    private int fishStartEnergy = 1;
    /**
     * Brut-Energie der Haie.
     */
    private int sharkBreedEnergy = 15;
    /**
     * Start-Energie der Haie.
     */
    private int sharkStartEnergy = 10;
    /**
     * Start-Energie der Haie.
     */
    private int sharkStarveEnergy;

    public WaTorRasterSimulation(final int width, final int height) {
        super(width, height);

        fillRaster(() -> new WaTorCell(this));
        reset();
    }

    /**
     * @return int[]; 0 = Anzahl Fische, 1 = Anzahl Haie
     */
    public int[] countFishesAndSharks() {
        this.fishCounter.reset();
        this.sharkCounter.reset();

        getCellStream().map(WaTorCell.class::cast).forEach(cell -> {
            if (cell.isFish()) {
                this.fishCounter.increment();
            }
            else if (cell.isShark()) {
                this.sharkCounter.increment();
            }
        });

        return new int[]{this.fishCounter.intValue(), this.sharkCounter.intValue()};
    }

    /**
     * Brut-Energie der Fische.
     */
    public int getFishBreedEnergy() {
        return this.fishBreedEnergy;
    }

    /**
     * Start-Energie der Fische.
     */
    public int getFishStartEnergy() {
        return this.fishStartEnergy;
    }

    /**
     * Brut-Energie der Haie.
     */
    public int getSharkBreedEnergy() {
        return this.sharkBreedEnergy;
    }

    /**
     * Start-Energie der Haie.
     */
    public int getSharkStartEnergy() {
        return this.sharkStartEnergy;
    }

    /**
     * Sterbe-Energie der Haie.
     */
    public int getSharkStarveEnergy() {
        return this.sharkStarveEnergy;
    }

    @Override
    public void nextGeneration() {
        getCellStream().forEach(Cell::nextGeneration);
        // nextGenerationNestedFor();
        // nextGenerationStreams();

        fireCompleted();
    }

    /**
     * Brut-Energie der Fische.
     *
     * @param fishBreedEnergy int
     */
    public void setFishBreedEnergy(final int fishBreedEnergy) {
        this.fishBreedEnergy = fishBreedEnergy;
    }

    /**
     * Start-Energie der Fische.
     *
     * @param fishStartEnergy int
     */
    public void setFishStartEnergy(final int fishStartEnergy) {
        this.fishStartEnergy = fishStartEnergy;
    }

    /**
     * Brut-Energie der Haie.
     *
     * @param sharkBreedEnergy int
     */
    public void setSharkBreedEnergy(final int sharkBreedEnergy) {
        this.sharkBreedEnergy = sharkBreedEnergy;
    }

    /**
     * Start-Energie der Haie.
     *
     * @param sharkStartEnergy int
     */
    public void setSharkStartEnergy(final int sharkStartEnergy) {
        this.sharkStartEnergy = sharkStartEnergy;
    }

    /**
     * Sterbe-Energie der Haie.
     *
     * @param sharkStarveEnergy int
     */
    public void setSharkStarveEnergy(final int sharkStarveEnergy) {
        this.sharkStarveEnergy = sharkStarveEnergy;
    }

    /**
     * Alte Berechnung.<br>
     * Richtung der Berechnung ändern, um Wellen-Fronten zu vermeiden.<br>
     */
    void nextGenerationNestedFor() {
        if (this.direction == 0) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 1) {
            for (int x = getWidth() - 1; x >= 0; x--) {
                for (int y = 0; y < getHeight(); y++) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 2) {
            for (int x = getWidth() - 1; x >= 0; x--) {
                for (int y = getHeight() - 1; y >= 0; y--) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 3) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = getHeight() - 1; y >= 0; y--) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }

        this.direction++;

        if (this.direction == 4) {
            this.direction = 0;
        }
    }

    /**
     * Alte Berechnung.<br>
     * Richtung der Berechnung ändern, um Wellen-Fronten zu vermeiden.<br>
     */
    void nextGenerationStreams() {
        if (this.direction == 0) {
            IntStream.range(0, getWidth())
                    .parallel()
                    .forEach(x -> {
                        for (int y = 0; y < getHeight(); y++) {
                            final WaTorCell cell = getCell(x, y);

                            if (cell != null) {
                                cell.nextGeneration();
                            }
                        }
                    });
        }
        else if (this.direction == 1) {
            IntStream.rangeClosed(getWidth() - 1, 0)
                    .parallel()
                    .forEach(x -> {
                        for (int y = 0; y < getHeight(); y++) {
                            final WaTorCell cell = getCell(x, y);

                            if (cell != null) {
                                cell.nextGeneration();
                            }
                        }
                    });
        }
        else if (this.direction == 2) {
            IntStream.rangeClosed(getWidth() - 1, 0)
                    .parallel()
                    .forEach(x -> {
                        for (int y = getHeight() - 1; y >= 0; y--) {
                            final WaTorCell cell = getCell(x, y);

                            if (cell != null) {
                                cell.nextGeneration();
                            }
                        }
                    });
        }
        else if (this.direction == 3) {
            IntStream.range(0, getWidth())
                    .parallel()
                    .forEach(x -> {
                        for (int y = getHeight() - 1; y >= 0; y--) {
                            final WaTorCell cell = getCell(x, y);

                            if (cell != null) {
                                cell.nextGeneration();
                            }
                        }
                    });
        }

        this.direction++;

        if (this.direction == 4) {
            this.direction = 0;
        }
    }

    @Override
    protected WaTorCell getCell(final int x, final int y) {
        return (WaTorCell) super.getCell(x, y);
    }

    @Override
    protected void reset(final int x, final int y) {
        // Zufällige Platzierung.
        final int rand = getRandom().nextInt(10);

        final WaTorCell cell = getCell(x, y);

        switch (rand) {
            case 3 -> {
                cell.setCellType(CellType.FISH);
                cell.setEnergy(getFishStartEnergy());

            }
            case 6 -> {
                cell.setCellType(CellType.SHARK);
                cell.setEnergy(getSharkStartEnergy());
            }

            default -> cell.setCellType(CellType.EMPTY);
        }
    }
}
