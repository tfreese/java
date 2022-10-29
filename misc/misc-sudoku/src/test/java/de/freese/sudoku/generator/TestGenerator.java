// Created: 07.11.2009
package de.freese.sudoku.generator;

/**
 * @author Thomas Freese
 */
public final class TestGenerator
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        testCheckBlock();
        testBlockIndex();
    }

    /**
     *
     */
    public static void testBlockIndex()
    {
        for (int x = 0; x < 25; x++)
        {
            for (int y = 0; y < 25; y++)
            {
                System.out.printf("[%2d,%2d] = %d-%d-%d%n", x, y, x % 5, y % 5, (x / 5) + (y / 5));
            }
        }
    }

    /**
     *
     */
    public static void testCheckBlock()
    {
        // SudokuGenerator generator = new SudokuGenerator();
        // generator.checkBox(TestGrids.GRID3_1, 4, 4, 9);
    }

    /**
     * Erstellt ein neues {@link TestGenerator} Object.
     */
    private TestGenerator()
    {
        super();
    }
}
