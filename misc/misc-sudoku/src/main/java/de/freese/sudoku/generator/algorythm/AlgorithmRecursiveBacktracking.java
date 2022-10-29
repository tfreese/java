// Created: 07.11.2009
package de.freese.sudoku.generator.algorythm;

/**
 * Algorithmus zum Erstellen von Sudoku-Rätseln mit rekursivem Backtracking.
 *
 * @author Thomas Freese
 */
public class AlgorithmRecursiveBacktracking implements SudokuAlgorithm
{
    /**
     * @see SudokuAlgorithm#create(int[][])
     */
    @Override
    public boolean create(final int[][] grid)
    {
        boolean emptyField = false;

        // Mögliche Zahlen zufällig generieren
        int[] shuffledNumbers = shuffleNumbers(grid.length);

        // Durch die Spalten gehen
        for (int x = 0; x < grid.length; x++)
        {
            // Durch die Zeilen gehen
            for (int y = 0; y < grid[0].length; y++)
            {
                if (grid[x][y] == 0)
                {
                    emptyField = true;

                    // Für alle möglichen Zahlen
                    for (int k = 0; k < grid.length; k++)
                    {
                        int value = shuffledNumbers[k] + 1;

                        if (isLegal(grid, x, y, value))
                        {
                            grid[x][y] = value;

                            // Versuchen den Rest aufzufüllen
                            if (create(grid))
                            {
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

    /**
     * Prüft, ob der Wert bereits in der Box existiert.
     *
     * @param grid int[][]
     * @param x int
     * @param y int
     * @param value int
     *
     * @return boolean
     */
    private boolean checkBox(final int[][] grid, final int x, final int y, final int value)
    {
        // Oberes, linke Ecke der Box herausfinden
        int blockSize = (int) Math.sqrt(grid.length);

        int xStart = (x / blockSize) * blockSize;
        int yStart = (y / blockSize) * blockSize;

        for (int a = xStart; a < (xStart + blockSize); a++)
        {
            for (int b = yStart; b < (yStart + blockSize); b++)
            {
                if (grid[a][b] == value)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Prüft, ob der Wert bereits in der Spalte existiert.
     *
     * @param grid int[][]
     * @param column int
     * @param value int
     *
     * @return boolean
     */
    private boolean checkColumn(final int[][] grid, final int column, final int value)
    {
        for (int y = 0; y < grid[column].length; y++)
        {
            if (grid[column][y] == value)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Prüft, ob der Wert bereits in der Zeile existiert.
     *
     * @param grid int[][]
     * @param row int
     * @param value int
     *
     * @return boolean
     */
    private boolean checkRow(final int[][] grid, final int row, final int value)
    {
        for (int[] element : grid)
        {
            if (element[row] == value)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Liefert true, wenn Value noch nicht in der Zeile, Spalte oder der Box vorhanden ist.
     *
     * @param grid int[][]
     * @param x int
     * @param y int
     * @param value int
     *
     * @return boolean
     */
    private boolean isLegal(final int[][] grid, final int x, final int y, final int value)
    {
        return !checkBox(grid, x, y, value) && !checkColumn(grid, x, value) && !checkRow(grid, y, value);
    }

    /**
     * Liefert ein Array der verwendeten Zahlen in zufälliger Reihenfolge.
     *
     * @param size int
     *
     * @return int[]
     */
    private int[] shuffleNumbers(final int size)
    {
        int[] list = new int[size];

        for (int i = 0; i < size; i++)
        {
            list[i] = i;
        }

        for (int i = 0; i < size; i++)
        {
            int r = (int) (Math.random() * size);
            int swap = list[r];
            list[r] = list[i];
            list[i] = swap;
        }

        return list;
    }
}
