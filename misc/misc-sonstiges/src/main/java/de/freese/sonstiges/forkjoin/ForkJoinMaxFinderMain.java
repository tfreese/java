// Created: 04.04.2012
package de.freese.sonstiges.forkjoin;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.forkjoin.finder.MaxFinder;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 *
 * @author Thomas Freese
 */
public final class ForkJoinMaxFinderMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinMaxFinderMain.class);

    public static void main(final String[] args) {
        final Random random = new SecureRandom();

        // Zufallsarray erstellen.
        final int[] array = new int[100_000_000];

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        final ForkJoinPool forkJoinPool = new ForkJoinPool();
        final MaxFinder finder = new MaxFinder(array, 1000, forkJoinPool);
        final int result = finder.find();
        LOGGER.info("Max = {}, Parallelism = {}", result, forkJoinPool.getParallelism());

        forkJoinPool.shutdown();
    }

    private ForkJoinMaxFinderMain() {
        super();
    }
}
