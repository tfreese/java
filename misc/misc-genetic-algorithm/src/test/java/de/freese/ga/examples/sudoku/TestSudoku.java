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
 * @since 21.06.2020
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSudoku {
    private static final SudokuConfig CONFIG = new SudokuConfig();

    private static SudokuChromosome chromosome;

    @BeforeAll
    static void beforeAll() throws Exception {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("sudoku_indices.txt")) {
            final List<String[]> puzzle = CONFIG.parsePuzzle(inputStream);
            CONFIG.setPuzzle(puzzle);

            chromosome = new SudokuChromosome(CONFIG);

            final List<SudokuGene> genes = puzzle.stream()
                    .flatMap(Stream::of)
                    .map(Integer::parseInt)
                    .map(index -> index + 1) // Im Sudoku gib's keine 0
                    .map(index -> new SudokuGene(index, false))
                    .toList();

            for (int i = 0; i < genes.size(); i++) {
                chromosome.setGene(i, genes.get(i));
            }
        }
    }

    @Test
    void testCalcFitness() {
        final double fitness = chromosome.calcFitnessValue();

        assertEquals(9963.0D, fitness);
    }

    @Test
    void testMaxFittness() {
        final double maxFitness = CONFIG.getMaxFitness();

        assertEquals(1215D, maxFitness);
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void testSumBlocks() {
        final int puzzleBlockSize = CONFIG.getPuzzleBlockSize();

        assertEquals(99D, chromosome.calcBlockFitness(0, puzzleBlockSize));
        assertEquals(126D, chromosome.calcBlockFitness(1, puzzleBlockSize));
        assertEquals(153D, chromosome.calcBlockFitness(2, puzzleBlockSize));

        assertEquals(342D, chromosome.calcBlockFitness(3, puzzleBlockSize));
        assertEquals(369D, chromosome.calcBlockFitness(4, puzzleBlockSize));
        assertEquals(396D, chromosome.calcBlockFitness(5, puzzleBlockSize));

        assertEquals(585D, chromosome.calcBlockFitness(6, puzzleBlockSize));
        assertEquals(612D, chromosome.calcBlockFitness(7, puzzleBlockSize));
        assertEquals(639D, chromosome.calcBlockFitness(8, puzzleBlockSize));
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void testSumColumns() {
        final int puzzleSize = CONFIG.getPuzzleSize();

        assertEquals(333D, chromosome.calcColumnFitness(0, puzzleSize));
        assertEquals(342D, chromosome.calcColumnFitness(1, puzzleSize));
        assertEquals(351D, chromosome.calcColumnFitness(2, puzzleSize));

        assertEquals(360D, chromosome.calcColumnFitness(3, puzzleSize));
        assertEquals(369D, chromosome.calcColumnFitness(4, puzzleSize));
        assertEquals(378D, chromosome.calcColumnFitness(5, puzzleSize));

        assertEquals(387D, chromosome.calcColumnFitness(6, puzzleSize));
        assertEquals(396D, chromosome.calcColumnFitness(7, puzzleSize));
        assertEquals(405D, chromosome.calcColumnFitness(8, puzzleSize));
    }

    /**
     * Von oben nach unten.
     */
    @Test
    void testSumRows() {
        assertEquals(45D, chromosome.calcRowFitness(0));
        assertEquals(126D, chromosome.calcRowFitness(1));
        assertEquals(207D, chromosome.calcRowFitness(2));

        assertEquals(288D, chromosome.calcRowFitness(3));
        assertEquals(369D, chromosome.calcRowFitness(4));
        assertEquals(450D, chromosome.calcRowFitness(5));

        assertEquals(531D, chromosome.calcRowFitness(6));
        assertEquals(612D, chromosome.calcRowFitness(7));
        assertEquals(693D, chromosome.calcRowFitness(8));
    }
}
