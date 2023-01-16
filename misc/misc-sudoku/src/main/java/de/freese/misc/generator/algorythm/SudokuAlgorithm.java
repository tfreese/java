// Created: 07.11.2009
package de.freese.misc.generator.algorythm;

/**
 * Interface für einen Algorithmus zum Erstellen von Sudoku-Rätseln.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SudokuAlgorithm
{
    /**
     * Füllt das Array mit den Zahlen.
     *
     * @return boolean, true, wenn das Rätsel gültig ist.
     */
    boolean create(int[][] grid);
}
