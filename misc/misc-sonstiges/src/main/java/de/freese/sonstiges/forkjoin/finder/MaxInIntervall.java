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
     * Schwellenwert, bei dem die Suche sequenziell durchgeführt wird.
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
        return this.result;
    }

    @Override
    protected void compute() {
        if ((this.end - this.start) < this.intervalThreshold) {
            this.result = findMaxSequentially();
        }
        else {
            int middle = (this.end - this.start) / 2;

            MaxInIntervall task1 = new MaxInIntervall(this.array, this.start, this.start + middle, this.intervalThreshold);
            MaxInIntervall task2 = new MaxInIntervall(this.array, this.start + middle, this.end, this.intervalThreshold);

            invokeAll(task1, task2);

            this.result = Math.max(task1.getResult(), task2.getResult());
        }
    }

    private int findMaxSequentially() {
        int max = Integer.MIN_VALUE;

        for (int i = this.start; i < this.end; i++) {
            int n = this.array[i];

            if (n > max) {
                max = n;
            }
        }

        return max;
    }
}
