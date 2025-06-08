// Created: 12.05.2012
package de.freese.sonstiges.forkjoin.fibonacci;

import java.io.Serial;
import java.util.concurrent.RecursiveTask;

/**
 * {@link RecursiveTask} für Fibonacci Algorithmus.
 *
 * @author Thomas Freese
 */
public class FibonacciTask extends RecursiveTask<Long> {
    /**
     * THRESHOLD, for sequential calculation.
     */
    private static final int THRESHOLD = 15;
    @Serial
    private static final long serialVersionUID = 67781993370162624L;

    private final int n;

    public FibonacciTask(final int n) {
        super();

        this.n = n;
    }

    @Override
    protected Long compute() {
        long result = 0L;

        if (n < THRESHOLD) {
            result = FibonacciCalculator.fibonacci(n);
        }
        else {
            final FibonacciTask task1 = new FibonacciTask(n - 1);
            final FibonacciTask task2 = new FibonacciTask(n - 2);
            task2.fork();

            result = task1.compute() + task2.join();
        }

        return result;
    }
}
