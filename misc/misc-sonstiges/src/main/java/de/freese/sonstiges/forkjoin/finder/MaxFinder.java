// Created: 04.04.2012
package de.freese.sonstiges.forkjoin.finder;

import java.util.concurrent.ForkJoinPool;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 *
 * @author Thomas Freese
 */
public class MaxFinder
{
    /**
     *
     */
    private final int[] array;
    /**
     *
     */
    private final ForkJoinPool forkJoinPool;
    /**
     *
     */
    private final int intervalThreshold;

    /**
     * Erstellt ein neues {@link MaxFinder} Object.
     *
     * @param array Array
     * @param intervalThreshold Schwellenwert, bei dem die Suche sequenziell durchgeführt wird.
     * @param forkJoinPool {@link ForkJoinPool}
     */
    public MaxFinder(final int[] array, final int intervalThreshold, final ForkJoinPool forkJoinPool)
    {
        super();

        this.array = array;
        this.intervalThreshold = intervalThreshold;
        this.forkJoinPool = forkJoinPool;
    }

    /**
     * @return int
     */
    public int find()
    {
        MaxInIntervall task = new MaxInIntervall(this.array, 0, this.array.length, this.intervalThreshold);
        this.forkJoinPool.invoke(task);

        return task.getResult();
    }
}
