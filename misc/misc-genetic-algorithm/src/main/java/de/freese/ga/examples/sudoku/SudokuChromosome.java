// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import de.freese.ga.Chromosome;

/**
 * @author Thomas Freese
 */
public class SudokuChromosome extends Chromosome {
    public SudokuChromosome(final SudokuConfig config) {
        super(config);
    }

    @Override
    public double calcFitnessValue() {
        final int puzzleSize = getConfig().getPuzzleSize();
        final int puzzleBlockSize = getConfig().getPuzzleBlockSize();

        double fitness = 0.0D;

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(this::calcRowFitness).sum();

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(c -> calcColumnFitness(c, puzzleSize)).sum();

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(b -> calcBlockFitness(b, puzzleBlockSize)).sum();

        // Soll: 405 x 3 = 1215
        return fitness;
    }

    @Override
    public SudokuGene getGene(final int index) {
        return (SudokuGene) super.getGene(index);
    }

    @Override
    public void mutate() {
        for (int i = 0; i < size(); i++) {
            if (getConfig().getRandom().nextDouble() < getConfig().getMutationRate()) {
                final int j = getConfig().getRandom().nextInt(size());

                final SudokuGene geneI = getGene(i);
                final SudokuGene geneJ = getGene(j);

                // Nur veränderbare.
                if (geneI.isMutable()) {
                    setGene(i, new SudokuGene(geneJ.getInteger(), true));
                }

                if (geneJ.isMutable()) {
                    setGene(j, new SudokuGene(geneI.getInteger(), true));
                }
            }
        }

        // IntStream.range(0, chromosome.size())
        //         .parallel()
        //         .forEach(i -> {
        //             if (getConfig().getRandom().nextDouble() < getConfig().getMutationRate()) {
        //                 final int j = getRandom().nextInt(chromosome.size());
        //
        //                 final SudokuGene geneI = chromosome.getGene(i);
        //                 final SudokuGene geneJ = chromosome.getGene(j);
        //
        //                 // Nur veränderbare.
        //                 if (geneI.isMutable()) {
        //                     chromosome.setGene(i, new SudokuGene(geneJ.getValue(), true));
        //                 }
        //
        //                 if (geneJ.isMutable()) {
        //                     chromosome.setGene(j, new SudokuGene(geneI.getValue(), true));
        //                 }
        //             }
        //         });
    }

    @Override
    public void populate() {
        final Map<Integer, SudokuGene> fixNumbers = getConfig().getFixNumbers();

        // Population pro Zeile testen.
        // final Set<Integer> set= IntStream.range(0, 9).collect(TreeSet::new, TreeSet::add,TreeSet::addAll);
        // final Set<Integer> set= IntStream.range(0, 9).boxed().collect(Collectors.toSet());

        for (int i = 0; i < size(); i++) {
            // Erst nach fest vorgegeben Zahlen suchen.
            SudokuGene gene = fixNumbers.get(i);

            if (gene == null) {
                // Dann welche generieren.
                final int n = getConfig().getRandom().nextInt(9) + 1;

                gene = new SudokuGene(n, true);
            }

            setGene(i, gene);
        }

        // IntStream.range(0, chromosome.size())
        //         .parallel()
        //         .forEach(i -> {
        //             // Erst nach fest vorgegeben Zahlen suchen.
        //             SudokuGene gene = this.fixNumbers.get(i);
        //
        //             if (gene == null) {
        //                 // Dann welche generieren.
        //                 final int n = getConfig().getRandom().nextInt(9) + 1;
        //
        //                 gene = new SudokuGene(n, true);
        //             }
        //
        //             genes[i] = gene;
        //         });
    }

    @Override
    public String toString() {
        final int puzzleSize = getConfig().getPuzzleSize();
        final int puzzleBlockSize = getConfig().getPuzzleBlockSize();

        final StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());

        for (int row = 0; row < puzzleSize; row++) {
            for (int col = 0; col < puzzleSize; col++) {
                final int index = (row * puzzleSize) + col;

                final SudokuGene gene = getGene(index);

                if (gene.isMutable()) {
                    sb.append(String.format(" %d ", gene.getInteger()));
                }
                else {
                    sb.append(String.format("(%d)", gene.getInteger()));
                }

                if (((col + 1) % puzzleBlockSize) == 0 && col < (puzzleSize - 1)) {
                    sb.append("|");
                }
                else {
                    sb.append(" ");
                }
            }

            sb.append(System.lineSeparator());

            if (((row + 1) % puzzleBlockSize) == 0 && row < (puzzleSize - 1)) {
                final char[] chars = new char[(puzzleBlockSize * 3) + 2];
                Arrays.fill(chars, '-');
                final String separator = new String(chars);

                sb.append(String.format("%s|%s|%s%n", separator, separator, separator));
            }
        }

        return sb.toString();
    }

    /**
     * Wie viele unterschiedliche Zahlen sind im Block und wie ist deren Summe ?<br>
     * Soll: bei 9 Zahlen 45 in Summe
     */
    double calcBlockFitness(final int block, final int puzzleBlockSize) {
        int start = switch (block) {
            case 0, 1, 2 -> block * puzzleBlockSize;
            case 3 -> 27;
            case 4 -> 30;
            case 5 -> 33;
            case 6 -> 54;
            case 7 -> 57;
            case 8 -> 60;
            default -> 0;
        };

        final Set<Integer> set = new HashSet<>();

        for (int i = start; i < (start + puzzleBlockSize); i++) {
            set.add(getGene(i).getInteger());
        }

        start += 9;

        for (int i = start; i < (start + puzzleBlockSize); i++) {
            set.add(getGene(i).getInteger());
        }

        start += 9;

        for (int i = start; i < (start + puzzleBlockSize); i++) {
            set.add(getGene(i).getInteger());
        }

        // double fitness = set.size();

        return set.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Wie viele unterschiedliche Zahlen sind in der Spalte und wie ist deren Summe ?<br>
     * Soll: bei 9 Zahlen 45 in Summe
     */
    double calcColumnFitness(final int column, final int puzzleSize) {
        final int start = column;

        final Set<Integer> set = new HashSet<>();

        for (int i = start; i < (puzzleSize * puzzleSize); i += puzzleSize) {
            set.add(getGene(i).getInteger());
        }

        // final double fitness = set.size();

        return set.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Wie viele unterschiedliche Zahlen sind in der Reihe und wie ist deren Summe ?<br>
     * Soll: bei 9 Zahlen 45 in Summe
     */
    double calcRowFitness(final int row) {
        final int start = row * 9;
        final int end = start + 9;

        final Set<Integer> set = new HashSet<>();

        for (int i = start; i < end; i++) {
            set.add(getGene(i).getInteger());
        }

        // final double fitness = set.size();

        return set.stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    protected SudokuConfig getConfig() {
        return (SudokuConfig) super.getConfig();
    }
}
