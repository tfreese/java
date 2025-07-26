// Created: 09.03.2021
package de.freese.simulationen.wator;

import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.Cell;
import de.freese.simulationen.wator.WaTorCell.CellType;

/**
 * Model of the WaTor-Simulation.<br>
 * <a href="http://de.academic.ru/dic.nsf/dewiki/1492493">WaTor-Simulation</a>
 *
 * @author Thomas Freese
 */
public class WaTorRasterSimulation extends AbstractRasterSimulation {
    private final LongAdder fishCounter = new LongAdder();
    private final LongAdder sharkCounter = new LongAdder();

    private int direction;
    private int fishBreedEnergy = 5;
    private int fishStartEnergy = 1;
    private int sharkBreedEnergy = 15;
    private int sharkStartEnergy = 10;
    private int sharkStarveEnergy;

    public WaTorRasterSimulation(final int width, final int height) {
        super(width, height);

        fillRaster(() -> new WaTorCell(this));
        reset();
    }

    /**
     * @return int[]; 0 = count of fishes, 1 = count of sharks
     */
    public int[] countFishesAndSharks() {
        fishCounter.reset();
        sharkCounter.reset();

        getCellStream().map(WaTorCell.class::cast).forEach(cell -> {
            if (cell.isFish()) {
                fishCounter.increment();
            }
            else if (cell.isShark()) {
                sharkCounter.increment();
            }
        });

        return new int[]{fishCounter.intValue(), sharkCounter.intValue()};
    }

    /**
     * Breed energie of fishes.
     */
    public int getFishBreedEnergy() {
        return fishBreedEnergy;
    }

    /**
     * Start energie of fishes.
     */
    public int getFishStartEnergy() {
        return fishStartEnergy;
    }

    /**
     * Breed energie of sharks.
     */
    public int getSharkBreedEnergy() {
        return sharkBreedEnergy;
    }

    /**
     * Start energie of sharks.
     */
    public int getSharkStartEnergy() {
        return sharkStartEnergy;
    }

    /**
     * Dying energie of sharks.
     */
    public int getSharkStarveEnergy() {
        return sharkStarveEnergy;
    }

    @Override
    public void nextGeneration() {
        getCellStream().forEach(Cell::nextGeneration);
        // nextGenerationNestedFor();
        // nextGenerationStreams();

        fireCompleted();
    }

    /**
     * Breed energie of fishes.
     *
     * @param fishBreedEnergy int
     */
    public void setFishBreedEnergy(final int fishBreedEnergy) {
        this.fishBreedEnergy = fishBreedEnergy;
    }

    /**
     * Start energie of fishes.
     *
     * @param fishStartEnergy int
     */
    public void setFishStartEnergy(final int fishStartEnergy) {
        this.fishStartEnergy = fishStartEnergy;
    }

    /**
     * Breed energie of sharks.
     *
     * @param sharkBreedEnergy int
     */
    public void setSharkBreedEnergy(final int sharkBreedEnergy) {
        this.sharkBreedEnergy = sharkBreedEnergy;
    }

    /**
     * Start energie of sharks.
     *
     * @param sharkStartEnergy int
     */
    public void setSharkStartEnergy(final int sharkStartEnergy) {
        this.sharkStartEnergy = sharkStartEnergy;
    }

    /**
     * Dying energy of sharks.
     *
     * @param sharkStarveEnergy int
     */
    public void setSharkStarveEnergy(final int sharkStarveEnergy) {
        this.sharkStarveEnergy = sharkStarveEnergy;
    }

    /**
     * Change the direction of the calculation, to avoid wave fronts
     */
    void nextGenerationNestedFor() {
        if (direction == 0) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (direction == 1) {
            for (int x = getWidth() - 1; x >= 0; x--) {
                for (int y = 0; y < getHeight(); y++) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (direction == 2) {
            for (int x = getWidth() - 1; x >= 0; x--) {
                for (int y = getHeight() - 1; y >= 0; y--) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (direction == 3) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = getHeight() - 1; y >= 0; y--) {
                    final WaTorCell cell = getCell(x, y);

                    if (cell != null) {
                        cell.nextGeneration();
                    }
                }
            }
        }

        direction++;

        if (direction == 4) {
            direction = 0;
        }
    }

    /**
     * Change the direction of the calculation, to avoid wave fronts
     */
    void nextGenerationStreams() {
        if (direction == 0) {
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
        else if (direction == 1) {
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
        else if (direction == 2) {
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
        else if (direction == 3) {
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

        direction++;

        if (direction == 4) {
            direction = 0;
        }
    }

    @Override
    protected WaTorCell getCell(final int x, final int y) {
        return (WaTorCell) super.getCell(x, y);
    }

    @Override
    protected void reset(final int x, final int y) {
        // Place randomly.
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
