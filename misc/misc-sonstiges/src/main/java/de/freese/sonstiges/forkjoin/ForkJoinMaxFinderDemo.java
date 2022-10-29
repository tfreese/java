// Created: 04.04.2012
package de.freese.sonstiges.forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import de.freese.sonstiges.forkjoin.finder.MaxFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 *
 * @author Thomas Freese
 */
public class ForkJoinMaxFinderDemo
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinMaxFinderDemo.class);

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        Random random = new Random();

        // Zufallsarray erstellen.
        int[] array = new int[100_000_000];

        for (int i = 0; i < array.length; i++)
        {
            array[i] = random.nextInt();
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MaxFinder finder = new MaxFinder(array, 1000, forkJoinPool);
        int result = finder.find();
        LOGGER.info("Max = {}, Parallelism = {}", result, forkJoinPool.getParallelism());

        forkJoinPool.shutdown();
    }
}
