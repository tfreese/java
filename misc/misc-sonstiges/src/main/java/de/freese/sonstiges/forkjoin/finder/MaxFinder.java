package de.freese.sonstiges.forkjoin.finder;

import java.util.concurrent.ForkJoinPool;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 *
 * @author Thomas Freese
 * @since 04.04.2012
 */
public class MaxFinder {
    private final int[] array;
    private final ForkJoinPool forkJoinPool;
    /**
     * THRESHOLD, for sequential calculation.
     */
    private final int intervalThreshold;

    public MaxFinder(final int[] array, final int intervalThreshold, final ForkJoinPool forkJoinPool) {
        super();

        this.array = array;
        this.intervalThreshold = intervalThreshold;
        this.forkJoinPool = forkJoinPool;
    }

    public int find() {
        final MaxInIntervall task = new MaxInIntervall(array, 0, array.length, intervalThreshold);
        forkJoinPool.invoke(task);

        return task.getResult();
    }
}
