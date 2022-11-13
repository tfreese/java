// Created: 04.04.2012
package de.freese.sonstiges.forkjoin;

import java.util.concurrent.ForkJoinPool;

import de.freese.sonstiges.forkjoin.fibonacci.FibonacciCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Berechnet den Fibonacci-Wert.
 *
 * @author Thomas Freese
 */
public final class ForkJoinFibonacciCalculatorMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinFibonacciCalculatorMain.class);

    public static void main(final String[] args)
    {
        int n = 50;
        long result;

        result = FibonacciCalculator.fibonacci(n);
        LOGGER.info("n = {}, Result = {}", n, result);

        // ForkJoin braucht signifikant länger durch das Erzeugen der vielen Tasks, steuerbar über FibonacciTask.THRESHOLD.
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FibonacciCalculator calculator = new FibonacciCalculator(n, forkJoinPool);
        result = calculator.calculate();
        LOGGER.info("n = {}, Result = {}, Parallelism = {}", n, result, forkJoinPool.getParallelism());

        forkJoinPool.shutdown();
    }

    private ForkJoinFibonacciCalculatorMain()
    {
        super();
    }
}
