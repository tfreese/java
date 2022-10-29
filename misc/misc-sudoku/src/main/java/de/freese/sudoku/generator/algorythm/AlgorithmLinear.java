// Created: 07.11.2009
package de.freese.sudoku.generator.algorythm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Algorithmus zum Erstellen von Sudoku-Rätseln.
 *
 * @author Thomas Freese
 * @deprecated Liefert fehlerhafte Rätsel
 */
@Deprecated
public class AlgorithmLinear implements SudokuAlgorithm
{
    /**
     *
     */
    private int blockSize = 0;
    /**
     *
     */
    private int[][] grid;
    /**
     *
     */
    private List<Integer> numberList;

    /**
     * @see SudokuAlgorithm#create(int[][])
     */
    @Override
    public boolean create(final int[][] grid)
    {
        this.grid = grid;
        this.blockSize = (int) Math.sqrt(grid.length);
        this.numberList = new ArrayList<>(grid.length);

        int currentRow = 0;

        // Versuche zum auflösen einer Zeile
        int[] trials = new int[grid.length];
        boolean traceOn = true;

        // Zeilenweisen füllen des Arrays
        while (currentRow < grid[0].length)
        {
            trials[currentRow]++;

            // Zeile generieren
            if (generateRow(currentRow))
            {
                if (traceOn)
                {
                    System.out.print("Row " + (currentRow + 1) + " generated after " + trials[currentRow] + " trial");

                    if (trials[currentRow] > 1)
                    {
                        System.out.print("s");
                    }

                    System.out.println(".");
                }

                currentRow++;
                continue;
            }

            // Generierung fehlgeschlagen, nochmal versuchen
            if (trials[currentRow] < (this.blockSize * this.blockSize * this.blockSize * 2))
            {
                continue;
            }

            // Generierung weiterhin fehlgeschlagen, alle Zeilen des Blocks nochmal erzeugen
            if (traceOn)
            {
                System.out.print("Quitting for row: " + (currentRow + 1));
            }

            while ((currentRow % this.blockSize) != 0)
            {
                trials[currentRow--] = 0;
            }

            trials[currentRow] = 0;

            if (traceOn)
            {
                System.out.println(". Starting over with row: " + (currentRow + 1) + ".");
            }
        }

        // Zahlen sind 0-based, auf 1 normalisieren
        for (int x = 0; x < grid.length; x++)
        {
            for (int y = 0; y < grid[0].length; y++)
            {
                grid[x][y]++;
            }
        }

        return true;
    }

    /**
     * Füllen der internen Liste mit allen noch verfügbaren Zahlen.
     *
     * @param row int
     * @param col int
     *
     * @return int, Anzahl verfügbarer Zahlen
     */
    private int fillArrayList(final int row, final int col)
    {
        boolean[] available = new boolean[this.grid.length];
        Arrays.fill(available, true);

        // Entfernen der Zahlen, die in der Zeile schon existieren
        for (int x = 0; x < row; x++)
        {
            available[this.grid[x][row]] = false;
        }

        // Entfernen der Zahlen, die in der Spalte schon existieren
        for (int y = 0; y < col; y++)
        {
            available[this.grid[col][y]] = false;
        }

        // Entfernen der Zahlen, die in der Box schon existieren
        Point rowRange = getRegionRowsOrCols(row);
        Point colRange = getRegionRowsOrCols(col);

        for (int x = rowRange.x; x < row; x++)
        {
            for (int y = colRange.x; y <= colRange.y; y++)
            {
                available[this.grid[x][y]] = false;
            }
        }

        // int x_start = (col / this.blockSize) * this.blockSize;
        // int y_start = (row / this.blockSize) * this.blockSize;
        //
        // for (int x = x_start; x < x_start + this.blockSize; x++)
        // {
        // for (int y = y_start; y < y_start + this.blockSize; y++)
        // {
        // available[this.grid[x][y]] = false;
        // }
        // }

        this.numberList.clear();

        // Füllen der Liste mit den restlichen verfügbaren Zahlen.
        for (int i = 0; i < this.grid.length; i++)
        {
            if (available[i])
            {
                this.numberList.add(i);
            }
        }

        return this.numberList.size();
    }

    /**
     * Versuchen eine Zeile aufzubauen.
     *
     * @param row int
     *
     * @return boolean
     */
    private boolean generateRow(final int row)
    {
        for (int col = 0; col < this.grid.length; col++)
        {
            // Keine verfügbaren Zahlen mehr, dann Abbruch
            if (fillArrayList(row, col) == 0)
            {
                return false;
            }

            // Zufällige frei Zahl zuweisen
            int index = (int) (Math.random() * this.numberList.size());
            this.grid[row][col] = this.numberList.remove(index);
        }

        return true;
    }

    /**
     * Liefert die erste und letzte Zeile/Spalte innerhalb des Blocks.
     *
     * @param rowOrCol int
     *
     * @return {@link Point}
     */
    private Point getRegionRowsOrCols(final int rowOrCol)
    {
        int x = (rowOrCol / this.blockSize) * this.blockSize;
        int y = (x + this.blockSize) - 1;

        return new Point(x, y);
    }
}
