// Created: 04.04.2012
package de.freese.sonstiges.forkjoin;

import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.forkjoin.fibonacci.FibonacciCalculator;

/**
 * Berechnet den Fibonacci-Wert.
 *
 * @author Thomas Freese
 */
public final class ForkJoinFibonacciCalculatorMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinFibonacciCalculatorMain.class);

    static void main() {
        final int n = 50;
        long result;

        result = FibonacciCalculator.fibonacci(n);
        LOGGER.info("n = {}, Result = {}", n, result);

        // ForkJoin needs significant longer  through the creation of the Tasks, configurable with FibonacciTask.THRESHOLD.
        final ForkJoinPool forkJoinPool = new ForkJoinPool();
        final FibonacciCalculator calculator = new FibonacciCalculator(n, forkJoinPool);
        result = calculator.calculate();
        LOGGER.info("n = {}, Result = {}, Parallelism = {}", n, result, forkJoinPool.getParallelism());

        forkJoinPool.shutdown();
    }

    private ForkJoinFibonacciCalculatorMain() {
        super();
    }
}
