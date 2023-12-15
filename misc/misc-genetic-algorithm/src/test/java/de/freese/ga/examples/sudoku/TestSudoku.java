// Created: 21.06.2020
package de.freese.ga.examples.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSudoku {
    private static final SudokuConfig config = new SudokuConfig();

    private static SudokuChromosome chromosome;

    @BeforeAll
    static void beforeAll() throws Exception {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("sudoku_indices.txt")) {
            final List<String[]> puzzle = config.parsePuzzle(inputStream);
            config.setPuzzle(puzzle);

            chromosome = new SudokuChromosome(config);

            // @formatter:off
            final List<SudokuGene> genes = puzzle.stream()
                .flatMap(Stream::of)
                .map(Integer::parseInt)
                .map(index -> index + 1) // Im Sudoku gib's keine 0
                .map(index -> new SudokuGene(index, false))
                .toList();
            // @formatter:on

            for (int i = 0; i < genes.size(); i++) {
                chromosome.setGene(i, genes.get(i));
            }
        }
    }

    @Test
    void testCalcFitness() {
        final double fitness = chromosome.calcFitnessValue();

        assertEquals(9963.0, fitness);
    }

    @Test
    void testMaxFittness() {
        final double maxFitness = config.getMaxFitness();

        assertEquals(1215, maxFitness);
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void testSumBlocks() {
        final int puzzleBlockSize = config.getPuzzleBlockSize();

        assertEquals(99, chromosome.calcBlockFitness(0, puzzleBlockSize));
        assertEquals(126, chromosome.calcBlockFitness(1, puzzleBlockSize));
        assertEquals(153, chromosome.calcBlockFitness(2, puzzleBlockSize));

        assertEquals(342, chromosome.calcBlockFitness(3, puzzleBlockSize));
        assertEquals(369, chromosome.calcBlockFitness(4, puzzleBlockSize));
        assertEquals(396, chromosome.calcBlockFitness(5, puzzleBlockSize));

        assertEquals(585, chromosome.calcBlockFitness(6, puzzleBlockSize));
        assertEquals(612, chromosome.calcBlockFitness(7, puzzleBlockSize));
        assertEquals(639, chromosome.calcBlockFitness(8, puzzleBlockSize));
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void testSumColumns() {
        final int puzzleSize = config.getPuzzleSize();

        assertEquals(333, chromosome.calcColumnFitness(0, puzzleSize));
        assertEquals(342, chromosome.calcColumnFitness(1, puzzleSize));
        assertEquals(351, chromosome.calcColumnFitness(2, puzzleSize));

        assertEquals(360, chromosome.calcColumnFitness(3, puzzleSize));
        assertEquals(369, chromosome.calcColumnFitness(4, puzzleSize));
        assertEquals(378, chromosome.calcColumnFitness(5, puzzleSize));

        assertEquals(387, chromosome.calcColumnFitness(6, puzzleSize));
        assertEquals(396, chromosome.calcColumnFitness(7, puzzleSize));
        assertEquals(405, chromosome.calcColumnFitness(8, puzzleSize));
    }

    /**
     * Von oben nach unten.
     */
    @Test
    void testSumRows() {
        assertEquals(45, chromosome.calcRowFitness(0));
        assertEquals(126, chromosome.calcRowFitness(1));
        assertEquals(207, chromosome.calcRowFitness(2));

        assertEquals(288, chromosome.calcRowFitness(3));
        assertEquals(369, chromosome.calcRowFitness(4));
        assertEquals(450, chromosome.calcRowFitness(5));

        assertEquals(531, chromosome.calcRowFitness(6));
        assertEquals(612, chromosome.calcRowFitness(7));
        assertEquals(693, chromosome.calcRowFitness(8));
    }
}
