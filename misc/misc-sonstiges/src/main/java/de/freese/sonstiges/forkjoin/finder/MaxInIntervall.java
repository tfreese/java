// Created: 04.04.2012
package de.freese.sonstiges.forkjoin.finder;

import java.io.Serial;
import java.util.concurrent.RecursiveAction;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 *
 * @author Thomas Freese
 */
public class MaxInIntervall extends RecursiveAction {
    @Serial
    private static final long serialVersionUID = -6829518464952417401L;

    private final int[] array;
    private final int end;
    /**
     * THRESHOLD, for sequential calculation.
     */
    private final int intervalThreshold;
    private final int start;

    private volatile int result;

    public MaxInIntervall(final int[] array, final int start, final int end, final int intervalThreshold) {
        super();

        this.array = array;
        this.start = start;
        this.end = end;
        this.intervalThreshold = intervalThreshold;
    }

    public int getResult() {
        return result;
    }

    @Override
    protected void compute() {
        if ((end - start) < intervalThreshold) {
            result = findMaxSequentially();
        }
        else {
            final int middle = (end - start) / 2;

            final MaxInIntervall task1 = new MaxInIntervall(array, start, start + middle, intervalThreshold);
            final MaxInIntervall task2 = new MaxInIntervall(array, start + middle, end, intervalThreshold);

            invokeAll(task1, task2);

            result = Math.max(task1.getResult(), task2.getResult());
        }
    }

    private int findMaxSequentially() {
        int max = Integer.MIN_VALUE;

        for (int i = start; i < end; i++) {
            final int n = array[i];

            if (n > max) {
                max = n;
            }
        }

        return max;
    }
}
