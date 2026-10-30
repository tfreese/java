package de.freese.dependency.update;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import de.freese.dependency.update.coordinate.Coordinate;
import de.freese.dependency.update.version.VersionResolver;

/**
 * @author Thomas Freese
 * @since 14.10.23
 */
final class QueryExecutor {
    private static final Map<String, List<String>> CACHE = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);
    /**
     * Too many parallel Requests will cause 'HTTP-429 - Too many Requests'.
     */
    private static final int PARALLELISM = 8;

    public static List<Coordinate> getUpdates(final VersionResolver versionResolver, final Set<URI> repositories, final List<Coordinate> coordinates) {
        // executeQueries(versionResolver, repositories, coordinates);
        //  executeQueriesByFlux(versionResolver, repositories, coordinates);
        executeQueriesByFixedExecutor(versionResolver, repositories, coordinates);
        // executeQueriesBySemaphore(versionResolver, repositories, coordinates);
        // executeQueriesByStreamParallel(versionResolver, repositories, coordinates);

        // CACHE.clear();

        return coordinates.stream()
                .filter(Coordinate::hasUpdate)
                .toList();
    }

    static void executeQueries(final VersionResolver versionResolver,
                               final Set<URI> repositories,
                               final List<Coordinate> coordinates) {
        coordinates.forEach(coordinate -> resolveVersions(versionResolver, repositories, coordinate));
    }

    static void executeQueriesByFixedExecutor(final VersionResolver versionResolver,
                                              final Set<URI> repositories,
                                              final List<Coordinate> coordinates) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(PARALLELISM, createThreadFactory())) {
            coordinates.forEach(coordinate -> executorService.execute(() -> resolveVersions(versionResolver, repositories, coordinate)));
        }
    }

    static void executeQueriesByFlux(final VersionResolver versionResolver,
                                     final Set<URI> repositories,
                                     final List<Coordinate> coordinates) {
        // final Scheduler scheduler = Schedulers.newParallel("Query");
        final Scheduler scheduler = Schedulers.newParallel(PARALLELISM, createThreadFactory());

        Flux.fromIterable(coordinates)
                .parallel(PARALLELISM)
                .runOn(scheduler)
                .doOnNext(coordinate -> resolveVersions(versionResolver, repositories, coordinate))
                .sequential()
                .blockLast();

        scheduler.disposeGracefully()
                .timeout(Duration.ofSeconds(1L))
                //.retry(5)
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L)))
                .onErrorResume(ex -> {
                    LOGGER.error(ex.getMessage(), ex);
                    
                    return Mono.fromRunnable(scheduler::dispose);
                })
                .block();
    }

    static void executeQueriesBySemaphore(final VersionResolver versionResolver,
                                          final Set<URI> repositories,
                                          final List<Coordinate> coordinates) {
        try (ExecutorService executorService = Executors.newCachedThreadPool(createThreadFactory())) {
            final Semaphore rateLimiter = new Semaphore(PARALLELISM, true);

            coordinates.forEach(coordinate -> {
                rateLimiter.acquireUninterruptibly();

                executorService.execute(() -> {
                    try {
                        resolveVersions(versionResolver, repositories, coordinate);
                    }
                    finally {
                        rateLimiter.release();
                    }
                });
            });

            // Wait until all are finished.
            rateLimiter.acquireUninterruptibly(PARALLELISM);
        }
    }

    static void executeQueriesByStreamParallel(final VersionResolver versionResolver,
                                               final Set<URI> repositories,
                                               final List<Coordinate> coordinates) {
        // Only use n Threads.
        //
        // For the behavior read Method: java.util.concurrent.ForkJoinTask.fork
        // "Arranges to asynchronously execute this task in the pool the current task is running in,
        // if applicable, or using the ForkJoinPool.commonPool() if not in ForkJoinPool."
        try (ExecutorService customThreadPool = new ForkJoinPool(PARALLELISM)) {
            // Analog
            // ExecutorService customThreadPool = Executors.newWorkStealingPool(parallelism);

            final Runnable runnable = () ->
                    coordinates.stream()
                            .parallel()
                            .forEach(coordinate -> resolveVersions(versionResolver, repositories, coordinate));

            try {
                customThreadPool.submit(runnable).get();
            }
            catch (final InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);

                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
            catch (final ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            // finally {
            //     // Prevent Memory-Leak.
            //     customThreadPool.shutdown();
            // }
        }
    }

    private static ThreadFactory createThreadFactory() {
        return Thread.ofPlatform().daemon().name("query-", 1L).factory();
        // return Thread.ofVirtual().name("query-", 1L).factory();
    }

    private static void resolveVersions(final VersionResolver versionResolver, final Set<URI> repositories, final Coordinate coordinate) {
        final List<String> versions = CACHE.computeIfAbsent(coordinate.getGroupIdAndArtifactId(),
                key -> versionResolver.resolveVersions(coordinate.getGroupId(), coordinate.getArtifactId(), repositories));

        coordinate.setVersions(versions);
    }

    private QueryExecutor() {
        super();
    }
}
