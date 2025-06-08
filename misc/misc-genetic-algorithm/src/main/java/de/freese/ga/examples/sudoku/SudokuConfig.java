// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import de.freese.ga.Config;

/**
 * @author Thomas Freese
 */
public class SudokuConfig extends Config {
    /**
     * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
     */
    private final Map<Integer, SudokuGene> fixNumbers = new TreeMap<>();

    private int puzzleBlockSize = (int) Math.sqrt(this.puzzleSize);
    private int puzzleSize = 9;

    public SudokuConfig() {
        super();
    }

    @Override
    public double getMaxFitness() {
        // Summe pro Zeile, Spalte und Block.<br>
        // Gaußsche Summenformel = (n² + n) / 2
        final int puzzleSum = (int) (Math.pow(puzzleSize, 2) + puzzleSize) / 2;

        double fitness = 0.0D;

        // 405: Summe aller Zeilen = 45 x 9
        fitness += puzzleSize * puzzleSum;

        // // 405: Summe aller Spalten = 45 x 9
        // fitness += puzzleSize * puzzleSum;
        //
        // // 405: Summe aller Blöcke = 45 x 9
        // fitness += puzzleSize * puzzleSum;

        // 405 x 3 = 1215
        fitness *= 3;

        return fitness;
    }

    public List<String[]> parsePuzzle(final InputStream inputStream) throws IOException {
        final List<String[]> puzzle = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(l -> !l.startsWith("-"))
                    .map(l -> l.replace("|", " "))
                    .map(l -> l.replace("_", "0"))
                    .map(l -> l.replace("x", "0"))
                    .map(l -> l.replace("X", "0"))
                    //.peek(System.out::println)
                    .map(l -> l.replace("  ", " "))
                    .map(l -> l.replace("  ", " "))
                    .map(String::strip)
                    //.peek(System.out::println)
                    .map(l -> l.split(" "))
                    //.peek(array -> System.out.println(Arrays.toString(array)))
                    .forEach(puzzle::add)
            ;
        }

        return puzzle;
    }

    /**
     * Ungekannte Zahlen sind als X markiert.<br>
     * Index 0 = Rows<br>
     * Index 1 = Columns
     */
    public void setPuzzle(final List<String[]> puzzle) {
        Objects.requireNonNull(puzzle, "puzzle required");

        if (puzzle.size() > 9 || puzzle.getFirst().length > 9) {
            throw new IllegalArgumentException("only puzzle with 9x9 format supported");
        }

        puzzleSize = puzzle.size();
        puzzleBlockSize = (int) Math.sqrt(puzzle.size());

        fixNumbers.clear();

        for (int row = 0; row < puzzleSize; row++) {
            final String[] columns = puzzle.get(row);

            for (int col = 0; col < puzzleSize; col++) {
                final int index = (row * puzzleSize) + col;

                final int number = Integer.parseInt(columns[col]);

                if (number > 0) {
                    fixNumbers.put(index, new SudokuGene(number, false));
                }
            }
        }

        setSizeChromosome(puzzleSize * puzzleSize);
    }

    Map<Integer, SudokuGene> getFixNumbers() {
        return fixNumbers;
    }

    int getPuzzleBlockSize() {
        return puzzleBlockSize;
    }

    int getPuzzleSize() {
        return puzzleSize;
    }
}
