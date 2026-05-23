// Created: 14.10.23
package de.freese.dependency.update;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
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
import de.freese.dependency.update.version.filter.VersionFilter;

/**
 * @author Thomas Freese
 */
final class QueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);

    /**
     * Too many parallel Requests will cause 'HTTP-429 - Too many Requests'.
     */
    private static final int PARALLELISM = 4;

    public static List<Coordinate> getUpdates(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        // executeQueries(versionResolver, coordinates, repositories);
        // executeQueriesByFlux(versionResolver, coordinates, repositories);
        executeQueriesByFixedExecutor(versionResolver, coordinates, repositories);
        // executeQueriesByFuture(versionResolver, coordinates, repositories);
        // executeQueriesBySemaphore(versionResolver, coordinates, repositories);
        // executeQueriesByStreamParallel(versionResolver, coordinates, repositories);

        return coordinates.stream()
                .filter(Coordinate::hasUpdate)
                .filter(coordinate -> !coordinate.getVersionNewest().startsWith(VersionFilter.EMPTY_VERSION))
                .toList();
    }

    static void executeQueries(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        coordinates.forEach(coordinate -> {
            final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
            coordinate.setVersionNewest(newestVersion);
        });
    }

    static void executeQueriesByFixedExecutor(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(PARALLELISM, createThreadFactory())) {
            coordinates.forEach(coordinate ->
                    executorService.execute(() -> {
                        final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
                        coordinate.setVersionNewest(newestVersion);
                    })
            );
        }
    }

    static void executeQueriesByFlux(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        final Scheduler scheduler = Schedulers.newParallel("Query");

        Flux.fromIterable(coordinates)
                .parallel(PARALLELISM)
                .runOn(scheduler)
                .doOnNext(coordinate -> {
                    final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
                    coordinate.setVersionNewest(newestVersion);
                })
                .sequential()
                .blockLast();

        scheduler.disposeGracefully()
                .timeout(Duration.ofSeconds(1L))
                //.retry(5)
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L)))
                .onErrorResume(ex -> Mono.fromRunnable(scheduler::dispose))
                .block();
    }

    static void executeQueriesByFuture(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(PARALLELISM, createThreadFactory())) {
            final List<Future<Void>> futureList = new ArrayList<>(PARALLELISM);

            for (final Coordinate coordinate : coordinates.stream().toList()) {
                if (futureList.size() == PARALLELISM) {
                    futureList.removeFirst().get();
                }

                final Future<Void> future = executorService.submit(() -> {
                    final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
                    coordinate.setVersionNewest(newestVersion);
                }, null);

                futureList.add(future);
            }

            // Wait until all are finished.
            for (final Future<Void> future : futureList) {
                future.get();
            }
        }
        catch (final InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (final ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    static void executeQueriesBySemaphore(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(PARALLELISM, createThreadFactory())) {
            final Semaphore rateLimiter = new Semaphore(PARALLELISM, true);

            coordinates.forEach(coordinate -> {
                rateLimiter.acquireUninterruptibly();

                executorService.execute(() -> {
                    try {
                        final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
                        coordinate.setVersionNewest(newestVersion);
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

    static void executeQueriesByStreamParallel(final VersionResolver versionResolver, final List<Coordinate> coordinates, final Set<URI> repositories) {
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
                            .forEach(coordinate -> {
                                final String newestVersion = versionResolver.findNewestVersion(coordinate.getGroupId(), coordinate.getArtifactId(), repositories);
                                coordinate.setVersionNewest(newestVersion);
                            });

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
    }

    private QueryExecutor() {
        super();
    }
}
