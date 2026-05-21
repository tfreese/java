// Created: 05 Apr. 2025
package de.freese.dependency.update.client;

import java.net.HttpRetryException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.RetryPolicy;
import dev.failsafe.function.CheckedSupplier;

/**
 * @author Thomas Freese
 */

public abstract class AbstractRetryableRepositoryClient extends AbstractRepositoryClient {
    private final FailsafeExecutor<Object> failsafeExecutor;

    protected AbstractRetryableRepositoryClient(final int maxRetries, final Duration retryInterval) {
        super();

        final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .withMaxRetries(maxRetries)
                .withDelay(retryInterval)
                .onRetry(event -> {
                    final Throwable lastException = event.getLastException();

                    if (lastException instanceof final HttpRetryException httpRetryException) {
                        getLogger().warn("Retry: {} - HTTP {} - {} - {}",
                                event.getExecutionCount(),
                                httpRetryException.responseCode(),
                                httpRetryException.getMessage(),
                                httpRetryException.getLocation()
                        );
                    } else if (lastException != null) {
                        final String error = Optional.ofNullable(lastException.getMessage()).orElse(lastException.getClass().getSimpleName());
                        getLogger().warn("retry: {} - {}", event.getExecutionCount(), error);
                    } else {
                        getLogger().warn("retry: {}", event.getExecutionCount());
                    }
                })
                .onFailure(event -> {
                    final Throwable throwable = event.getException();

                    if (throwable != null) {
                        getLogger().error(throwable.getMessage(), throwable);
                    } else {
                        getLogger().error(event.toString());
                    }
                })
                .build();

        failsafeExecutor = Failsafe.with(retryPolicy);
    }

    @Override
    public final boolean exist(final URI uri) {
        final CheckedSupplier<Boolean> checkedSupplier = () -> executeExist(uri);

        return failsafeExecutor.get(checkedSupplier);
    }

    @Override
    public final List<String> getVersionsByMavenSearch(final URI uri) {
        final CheckedSupplier<List<String>> checkedSupplier = () -> executeVersionsByMavenSearch(uri);

        return failsafeExecutor.get(checkedSupplier);
    }

    @Override
    public final List<String> getVersionsByMetaData(final URI uri) {
        final CheckedSupplier<List<String>> checkedSupplier = () -> executeVersionsByMetaData(uri);

        return failsafeExecutor.get(checkedSupplier);
    }

    protected abstract boolean executeExist(URI uri);

    protected abstract List<String> executeVersionsByMavenSearch(URI uri);

    protected abstract List<String> executeVersionsByMetaData(URI uri);
}
