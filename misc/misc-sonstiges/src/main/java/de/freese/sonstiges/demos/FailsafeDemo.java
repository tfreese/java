package de.freese.sonstiges.demos;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.failsafe.Bulkhead;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.Fallback;
import dev.failsafe.RetryPolicy;
import dev.failsafe.Timeout;
import dev.failsafe.function.CheckedRunnable;
import dev.failsafe.function.CheckedSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.NamedThreadFactory;

/**
 * @author Thomas Freese
 */
public final class FailsafeDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeDemo.class);

    public static void main(final String[] args) throws Exception {
        final CircuitBreaker<Object> circuitBreaker = CircuitBreaker.builder()
                //.handle(SQLException.class) // Alle Exceptions von diesem Typ werden als Fehler behandelt.
                //.withFailureThreshold(3, 5) // Öffnen, wenn 3 von 5 Ausführungen Fehler erzeugen.
                .withFailureThreshold(3, Duration.ofSeconds(1)) // Öffnen, wenn 3 Fehler im Zeitraum auftreten.
                .withDelay(Duration.ofSeconds(1)) // Zeitraum nach Öffnung bis es in den Half-Open State geht.
                .withSuccessThreshold(3, 5) // Schliessen, wenn 3 von 5 Ausführungen im Half-Open State keine Fehler erzeugen.
                .onClose(event -> LOGGER.info("Closed after {}", event.getPreviousState()))
                .onHalfOpen(event -> LOGGER.info("Half-Open after {}", event.getPreviousState()))
                .onOpen(event -> LOGGER.info("Open after {}", event.getPreviousState()))
                .onFailure(event -> LOGGER.error("onFailure: {}", event.getException().getMessage()))
                //.onSuccess(event -> LOGGER.info("Success: {}", event.getResult()))
                .build();

        fallback(circuitBreaker);
        System.out.println();

        circuitBreaker.close();
        // ipBlock(circuitBreaker);
    }

    static void fallback(final CircuitBreaker<Object> circuitBreaker) throws Exception {
        final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .withMaxRetries(2)
                .withDelay(Duration.ofSeconds(1L))
                .onRetry(event -> {
                    // if (event.getExecutionCount() == 0) {
                    //     return;
                    // }

                    LOGGER.info("RetryPolicy onRetry {}", event.getExecutionCount());
                })
                .build();

        // Restrict concurrent executions on a resource.
        final Bulkhead<Object> bulkhead = Bulkhead.builder(10)
                .withMaxWaitTime(Duration.ofMillis(500L))
                //.onFailure(event -> LOGGER.error("Bulkhead onFailure: {} ms", event.getElapsedTime().toMillis()))
                .build();

        // Permits 100 executions per second.
        // final RateLimiter<Object> rateLimiter = RateLimiter.smoothBuilder(100, Duration.ofSeconds(1)).withMaxWaitTime(Duration.ofSeconds(1)).build();
        // Permits an execution every 10 millis.
        // final RateLimiter<Object> rateLimiter = RateLimiter.smoothBuilder(Duration.ofMillis(10)).withMaxWaitTime(Duration.ofSeconds(1)).build();

        final Timeout<Object> timeout = Timeout.builder(Duration.ofMillis(50L))
                .onFailure(event -> LOGGER.error("Timeout onFailure: {} ms", event.getElapsedTime().toMillis()))
                .build();

        final Fallback<Object> fallback = Fallback.of("fallback");

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3, new NamedThreadFactory("scheduler-%d"));

        // Ausführung in umgekehrter Reihenfolge: Timeout -> Bulkhead/RateLimiter -> CircuitBreaker -> RetryPolicy -> Fallback
        final FailsafeExecutor<Object> failsafeExecutor = Failsafe.with(fallback, retryPolicy, circuitBreaker, bulkhead, timeout)
                .with(scheduledExecutorService);

        final CheckedSupplier<String> checkedSupplier = () -> {
            if (System.currentTimeMillis() % 3 == 0) {
                throw new RuntimeException("Test Exception");
            }

            TimeUnit.MILLISECONDS.sleep(50);

            return "value";
        };

        for (int i = 0; i < 20; i++) {
            // final String result = failsafeExecutor.get(checkedSupplier);
            final String result = failsafeExecutor.getAsync(checkedSupplier).get();

            LOGGER.info("Result = {}", result);

            TimeUnit.MILLISECONDS.sleep(200);
        }

        printMetrics(circuitBreaker);
    }

    static void ipBlock(final CircuitBreaker<Object> circuitBreaker) throws Exception {
        final Fallback<Object> fallback = Fallback.ofException(event -> new Exception("ERROR: Your IP is blocked !", event.getLastException()));

        // Ausführung in umgekehrter Reihenfolge: CircuitBreaker -> Fallback
        final FailsafeExecutor<Object> failsafeExecutor = Failsafe.with(fallback, circuitBreaker);

        final CheckedRunnable checkedRunnable = () -> {
            if (circuitBreaker.isOpen() || circuitBreaker.isHalfOpen()) {
                return;
            }

            if (System.currentTimeMillis() % 3 == 0) {
                throw new RuntimeException("Test Exception");
            }
        };

        for (int i = 0; i < 20; i++) {
            try {
                failsafeExecutor.run(checkedRunnable);
            }
            catch (FailsafeException ex) {
                final Throwable cause = ex.getCause();

                if (cause != null) {
                    LOGGER.error(cause.getMessage());
                }
            }
            catch (RuntimeException ex) {
                LOGGER.error(ex.getMessage());
            }

            // if (circuitBreaker.isClosed()) {
            //     if (System.currentTimeMillis() % 3 == 0) {
            //         circuitBreaker.recordFailure();
            //     }
            // }
            // else {
            //     circuitBreaker.recordSuccess();
            // }

            if (circuitBreaker.isOpen()) {
                LOGGER.info("IP is blocked");
            }
            else {
                LOGGER.info("IP is open");
            }

            TimeUnit.MILLISECONDS.sleep(200);
        }

        printMetrics(circuitBreaker);
    }

    private static void printMetrics(final CircuitBreaker<?> circuitBreaker) {
        System.out.println("ExecutionCount = " + circuitBreaker.getExecutionCount());
        System.out.println("FailureCount = " + circuitBreaker.getFailureCount());
        System.out.println("FailureRate = " + circuitBreaker.getFailureRate());
        System.out.println("SuccessCount = " + circuitBreaker.getSuccessCount());
        System.out.println("SuccessRate = " + circuitBreaker.getSuccessRate());
    }

    private FailsafeDemo() {
        super();
    }
}
