// Created: 07.11.2009
package de.freese.misc.generator.algorythm;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Algorithmus zum Erstellen von Sudoku-Rätseln.
 *
 * @author Thomas Freese
 * @deprecated Liefert fehlerhafte Rätsel
 */
@Deprecated(since = "now")
public class AlgorithmLinear implements SudokuAlgorithm {
    private final Random random = new SecureRandom();

    private int blockSize;
    private int[][] grid;
    private List<Integer> numberList;

    @Override
    public boolean create(final int[][] grid) {
        this.grid = grid;
        blockSize = (int) Math.sqrt(grid.length);
        numberList = new ArrayList<>(grid.length);

        int currentRow = 0;

        // Versuche zum Auflösen einer Zeile
        final int[] trials = new int[grid.length];
        final boolean traceOn = true;

        // Zeilenweisen füllen des Arrays
        while (currentRow < grid[0].length) {
            trials[currentRow]++;

            // Zeile generieren
            if (generateRow(currentRow)) {
                if (traceOn) {
                    System.out.print("Row " + (currentRow + 1) + " generated after " + trials[currentRow] + " trial");

                    if (trials[currentRow] > 1) {
                        System.out.print("s");
                    }

                    System.out.println(".");
                }

                currentRow++;
                continue;
            }

            // Generierung fehlgeschlagen, nochmal versuchen
            if (trials[currentRow] < (blockSize * blockSize * blockSize * 2)) {
                continue;
            }

            // Generierung weiterhin fehlgeschlagen, alle Zeilen des Blocks nochmal erzeugen
            if (traceOn) {
                System.out.print("Quitting for row: " + (currentRow + 1));
            }

            while ((currentRow % blockSize) != 0) {
                trials[currentRow--] = 0;
            }

            trials[currentRow] = 0;

            if (traceOn) {
                System.out.println(". Starting over with row: " + (currentRow + 1) + ".");
            }
        }

        // Zahlen sind 0-based, auf 1 normalisieren
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y]++;
            }
        }

        return true;
    }

    /**
     * Füllen der internen Liste mit allen noch verfügbaren Zahlen.
     *
     * @return int, Anzahl verfügbarer Zahlen
     */
    private int fillArrayList(final int row, final int col) {
        final boolean[] available = new boolean[grid.length];
        Arrays.fill(available, true);

        // Entfernen der Zahlen, die in der Zeile schon existieren
        for (int x = 0; x < row; x++) {
            available[grid[x][row]] = false;
        }

        // Entfernen der Zahlen, die in der Spalte schon existieren
        for (int y = 0; y < col; y++) {
            available[grid[col][y]] = false;
        }

        // Entfernen der Zahlen, die in der Box schon existieren
        final Point rowRange = getRegionRowsOrCols(row);
        final Point colRange = getRegionRowsOrCols(col);

        for (int x = rowRange.x; x < row; x++) {
            for (int y = colRange.x; y <= colRange.y; y++) {
                available[grid[x][y]] = false;
            }
        }

        // int x_start = (col / blockSize) * blockSize;
        // int y_start = (row / blockSize) * blockSize;
        //
        // for (int x = x_start; x < x_start + blockSize; x++) {
        // for (int y = y_start; y < y_start + blockSize; y++) {
        // available[grid[x][y]] = false;
        // }
        // }

        numberList.clear();

        // Füllen der Liste mit den restlichen verfügbaren Zahlen.
        for (int i = 0; i < grid.length; i++) {
            if (available[i]) {
                numberList.add(i);
            }
        }

        return numberList.size();
    }

    /**
     * Versuchen eine Zeile aufzubauen.
     */
    private boolean generateRow(final int row) {
        for (int col = 0; col < grid.length; col++) {
            // Keine verfügbaren Zahlen mehr, dann Abbruch.
            if (fillArrayList(row, col) == 0) {
                return false;
            }

            // Zufällige frei Zahl zuweisen.
            final int index = random.nextInt(numberList.size());
            grid[row][col] = numberList.remove(index);
        }

        return true;
    }

    /**
     * Liefert die erste und letzte Zeile/Spalte innerhalb des Blocks.
     */
    private Point getRegionRowsOrCols(final int rowOrCol) {
        final int x = (rowOrCol / blockSize) * blockSize;
        final int y = (x + blockSize) - 1;

        return new Point(x, y);
    }
}
