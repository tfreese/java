// Created: 07.11.2009
package de.freese.misc.generator.algorythm;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Algorithmus zum Erstellen von Sudoku-Rätseln mit rekursivem Backtracking.
 *
 * @author Thomas Freese
 */
public class AlgorithmRecursiveBacktracking implements SudokuAlgorithm {
    private static final Random RANDOM = new SecureRandom();

    /**
     * Prüft, ob der Wert bereits in der Box existiert.
     */
    private static boolean checkBox(final int[][] grid, final int x, final int y, final int value) {
        // Oberes, linke Ecke der Box herausfinden
        final int blockSize = (int) Math.sqrt(grid.length);

        final int xStart = (x / blockSize) * blockSize;
        final int yStart = (y / blockSize) * blockSize;

        for (int a = xStart; a < (xStart + blockSize); a++) {
            for (int b = yStart; b < (yStart + blockSize); b++) {
                if (grid[a][b] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Prüft, ob der Wert bereits in der Spalte existiert.
     */
    private static boolean checkColumn(final int[][] grid, final int column, final int value) {
        for (int y = 0; y < grid[column].length; y++) {
            if (grid[column][y] == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Prüft, ob der Wert bereits in der Zeile existiert.
     */
    private static boolean checkRow(final int[][] grid, final int row, final int value) {
        for (int[] element : grid) {
            if (element[row] == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Liefert true, wenn Value noch nicht in der Zeile, Spalte oder der Box vorhanden ist.
     */
    private static boolean isLegal(final int[][] grid, final int x, final int y, final int value) {
        return !checkBox(grid, x, y, value) && !checkColumn(grid, x, value) && !checkRow(grid, y, value);
    }

    /**
     * Liefert ein Array der verwendeten Zahlen in zufälliger Reihenfolge.
     */
    private static int[] shuffleNumbers(final int size) {
        final int[] list = new int[size];

        for (int i = 0; i < size; i++) {
            list[i] = i;
        }

        for (int i = 0; i < size; i++) {
            final int r = RANDOM.nextInt(size);
            final int swap = list[r];
            list[r] = list[i];
            list[i] = swap;
        }

        return list;
    }

    @Override
    public boolean create(final int[][] grid) {
        boolean emptyField = false;

        // Mögliche Zahlen zufällig generieren.
        final int[] shuffledNumbers = shuffleNumbers(grid.length);

        // Durch die Spalten gehen.
        for (int x = 0; x < grid.length; x++) {
            // Durch die Zeilen gehen.
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == 0) {
                    emptyField = true;

                    // Für alle möglichen Zahlen.
                    for (int k = 0; k < grid.length; k++) {
                        final int value = shuffledNumbers[k] + 1;

                        if (isLegal(grid, x, y, value)) {
                            grid[x][y] = value;

                            // Versuchen den Rest aufzufüllen.
                            if (create(grid)) {
                                return true;
                            }

                            // Backtracking
                            grid[x][y] = 0;
                        }
                    }

                    // Ungültiges Rätsel -> Backtracking
                    return false;
                }
            }
        }

        // Fertig !
        return !emptyField;
    }
}
