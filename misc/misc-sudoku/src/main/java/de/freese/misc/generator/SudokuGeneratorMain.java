// Created: 07.11.2009
package de.freese.misc.generator;

import java.util.Objects;

import de.freese.misc.generator.algorythm.AlgorithmRecursiveBacktracking;
import de.freese.misc.generator.algorythm.SudokuAlgorithm;

/**
 * Erstellt mit einem {@link SudokuAlgorithm} ein gültiges Rätsel.
 *
 * @author Thomas Freese
 */
public final class SudokuGeneratorMain {
    public static void main(final String[] args) {
        final SudokuGeneratorMain generator = new SudokuGeneratorMain(new AlgorithmRecursiveBacktracking());

        // Liefert fehlerhafte Rätsel
        // final SudokuGeneratorMain generator = new SudokuGeneratorMain(new AlgorithmLinear());

        final int[][] grid = generator.create(3);
        toString(grid);
    }

    public static void toString(final int[][] grid) {
        final int blockSize = (int) Math.sqrt(grid.length);

        final StringBuilder sb = new StringBuilder();

        for (int x = 0; x < grid.length; x++) {
            if ((x % blockSize) == 0) {
                sb.append("-".repeat((grid.length * 3) + blockSize + 1));

                sb.append(System.lineSeparator());
            }

            for (int y = 0; y < grid[0].length; y++) {
                if ((y % blockSize) == 0) {
                    sb.append("|");
                }

                sb.append(String.format("%2d ", grid[x][y]));
            }

            sb.append("|").append(System.lineSeparator());
        }

        sb.append("-".repeat((grid.length * 3) + blockSize + 1));

        sb.append(System.lineSeparator());

        System.out.println(sb);
    }

    private final SudokuAlgorithm algorithm;

    private SudokuGeneratorMain(final SudokuAlgorithm algorithm) {
        super();

        this.algorithm = Objects.requireNonNull(algorithm, "algorithm required");
    }

    /**
     * Erstellt das Rätsel einer bestimmten Blockgrösse und liefert das Array.
     */
    public int[][] create(final int blockSize) {
        final int n = blockSize * blockSize;

        final int[][] grid = new int[n][n];

        return create(grid);
    }

    /**
     * Füllt das Array mit Zahlen zu einem gültigen Rätsel.
     */
    public int[][] create(final int[][] grid) {
        if (grid == null) {
            throw new NullPointerException();
        }

        if (grid.length == 0) {
            throw new IllegalArgumentException("Array ist leer !");
        }

        if (grid.length != grid[0].length) {
            final String text = String.format("Array ist falsch dimensioniert: x=%d, y=%d !", grid.length, grid[0].length);

            throw new IllegalArgumentException(text);
        }

        final double blockSize = Math.sqrt(grid.length);

        if ((blockSize - Math.floor(blockSize)) != 0.0D) {
            throw new IllegalArgumentException("Array benötigt ganzzahlige Wurzel der Dimensionen !");
        }

        // if (blockSize > 4) {
        // this.algorithm = new AlgorithmLinear();
        // }
        // else {
        // this.algorithm = new AlgorithmRecursiveBacktracking();
        // }

        while (!this.algorithm.create(grid)) {
            System.out.println("Rätsel ungültig");
        }

        return grid;
    }
}
