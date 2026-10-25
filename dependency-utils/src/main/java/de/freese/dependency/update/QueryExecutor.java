package de.freese.dependency.update;

import java.net.URI;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
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
import de.freese.dependency.update.coordinate.GroupArtifactId;
import de.freese.dependency.update.version.VersionResolver;
import de.freese.dependency.update.version.filter.VersionFilter;

/**
 * @author Thomas Freese
 * @since 14.10.23
 */
final class QueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);

    /**
     * Too many parallel Requests will cause 'HTTP-429 - Too many Requests'.
     */
    private static final int PARALLELISM = 8;

    public static List<Coordinate> getUpdates(final VersionResolver versionResolver, final Set<URI> repositories, final List<Coordinate> coordinates) {
        final List<GroupArtifactId> groupArtifactIdList = coordinates.stream()
                // .map(GroupArtifactId.class::cast)
                .map(coordinate -> new GroupArtifactId(coordinate.getGroupId(), coordinate.getArtifactId()))
                .distinct()
                .sorted(Comparator.comparing(GroupArtifactId::toString))
                .toList();

        // final Map<String, String> versionMap = executeQueries(versionResolver, repositories, groupArtifactIdList);
        // final Map<String, String> versionMap = executeQueriesByFlux(versionResolver, repositories, groupArtifactIdList);
        final Map<String, String> versionMap = executeQueriesByFixedExecutor(versionResolver, repositories, groupArtifactIdList);
        // final Map<String, String> versionMap = executeQueriesBySemaphore(versionResolver, repositories, groupArtifactIdList);
        // final Map<String, String> versionMap = executeQueriesByStreamParallel(versionResolver, repositories, groupArtifactIdList);

        coordinates.forEach(coordinate -> {
            final String newestVersion = versionMap.get(coordinate.getGroupIdAndArtifactId());
            coordinate.setVersionNewest(newestVersion);
        });

        return coordinates.stream()
                .filter(Coordinate::hasUpdate)
                .filter(coordinate -> {
                    final String versionNewest = coordinate.getVersionNewest();

                    return versionNewest != null && !versionNewest.startsWith(VersionFilter.EMPTY_VERSION);
                })
                .toList();
    }

    static Map<String, String> executeQueries(final VersionResolver versionResolver,
                                              final Set<URI> repositories,
                                              final List<GroupArtifactId> groupArtifactIds) {
        final Map<String, String> versionMap = HashMap.newHashMap(groupArtifactIds.size());

        groupArtifactIds.forEach(groupArtifactId -> {
            final String newestVersion = versionResolver.findNewestVersion(groupArtifactId.getGroupId(), groupArtifactId.getArtifactId(), repositories);
            versionMap.put(groupArtifactId.getGroupIdAndArtifactId(), newestVersion);
        });

        return versionMap;
    }

    static Map<String, String> executeQueriesByFixedExecutor(final VersionResolver versionResolver,
                                                             final Set<URI> repositories,
                                                             final List<GroupArtifactId> groupArtifactIds) {
        final Map<String, String> versionMap = new ConcurrentHashMap<>(groupArtifactIds.size());

        try (ExecutorService executorService = Executors.newFixedThreadPool(PARALLELISM, createThreadFactory())) {
            groupArtifactIds.forEach(groupArtifactId ->
                    executorService.execute(() -> {
                        final String newestVersion = versionResolver.findNewestVersion(groupArtifactId.getGroupId(), groupArtifactId.getArtifactId(), repositories);
                        versionMap.put(groupArtifactId.getGroupIdAndArtifactId(), newestVersion);
                    })
            );
        }

        return versionMap;
    }

    static Map<String, String> executeQueriesByFlux(final VersionResolver versionResolver,
                                                    final Set<URI> repositories,
                                                    final List<GroupArtifactId> groupArtifactIds) {
        final Map<String, String> versionMap = new ConcurrentHashMap<>(groupArtifactIds.size());

        // final Scheduler scheduler = Schedulers.newParallel("Query");
        final Scheduler scheduler = Schedulers.newParallel(PARALLELISM, createThreadFactory());

        Flux.fromIterable(groupArtifactIds)
                .parallel(PARALLELISM)
                .runOn(scheduler)
                .doOnNext(groupArtifactId -> {
                    final String newestVersion = versionResolver.findNewestVersion(groupArtifactId.getGroupId(), groupArtifactId.getArtifactId(), repositories);
                    versionMap.put(groupArtifactId.getGroupIdAndArtifactId(), newestVersion);
                })
                .sequential()
                .blockLast();

        scheduler.disposeGracefully()
                .timeout(Duration.ofSeconds(1L))
                //.retry(5)
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L)))
                .onErrorResume(ex -> Mono.fromRunnable(scheduler::dispose))
                .block();

        return versionMap;
    }

    static Map<String, String> executeQueriesBySemaphore(final VersionResolver versionResolver,
                                                         final Set<URI> repositories,
                                                         final List<GroupArtifactId> groupArtifactIds) {
        final Map<String, String> versionMap = new ConcurrentHashMap<>(groupArtifactIds.size());

        try (ExecutorService executorService = Executors.newCachedThreadPool(createThreadFactory())) {
            final Semaphore rateLimiter = new Semaphore(PARALLELISM, true);

            groupArtifactIds.forEach(groupArtifactId -> {
                rateLimiter.acquireUninterruptibly();

                executorService.execute(() -> {
                    try {
                        final String newestVersion = versionResolver.findNewestVersion(groupArtifactId.getGroupId(), groupArtifactId.getArtifactId(), repositories);
                        versionMap.put(groupArtifactId.getGroupIdAndArtifactId(), newestVersion);
                    }
                    finally {
                        rateLimiter.release();
                    }
                });
            });

            // Wait until all are finished.
            rateLimiter.acquireUninterruptibly(PARALLELISM);
        }

        return versionMap;
    }

    static Map<String, String> executeQueriesByStreamParallel(final VersionResolver versionResolver,
                                                              final Set<URI> repositories,
                                                              final List<GroupArtifactId> groupArtifactIds) {
        final Map<String, String> versionMap = new ConcurrentHashMap<>(groupArtifactIds.size());

        // Only use n Threads.
        //
        // For the behavior read Method: java.util.concurrent.ForkJoinTask.fork
        // "Arranges to asynchronously execute this task in the pool the current task is running in,
        // if applicable, or using the ForkJoinPool.commonPool() if not in ForkJoinPool."
        try (ExecutorService customThreadPool = new ForkJoinPool(PARALLELISM)) {
            // Analog
            // ExecutorService customThreadPool = Executors.newWorkStealingPool(parallelism);

            final Runnable runnable = () ->
                    groupArtifactIds.stream()
                            .parallel()
                            .forEach(groupArtifactId -> {
                                final String newestVersion = versionResolver.findNewestVersion(groupArtifactId.getGroupId(), groupArtifactId.getArtifactId(), repositories);
                                versionMap.put(groupArtifactId.getGroupIdAndArtifactId(), newestVersion);
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

        return versionMap;
    }

    private static ThreadFactory createThreadFactory() {
        return Thread.ofPlatform().daemon().name("query-", 1L).factory();
        // return Thread.ofVirtual().name("query-", 1L).factory();
    }

    private QueryExecutor() {
        super();
    }
}
