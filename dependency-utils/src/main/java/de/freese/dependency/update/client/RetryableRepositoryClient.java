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
public final class RetryableRepositoryClient extends AbstractRepositoryClientDecorator {
    private final FailsafeExecutor<Object> failsafeExecutor;

    public RetryableRepositoryClient(final RepositoryClient delegate, final int maxRetries, final Duration retryInterval) {
        super(delegate);

        final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .withMaxRetries(maxRetries)
                // .withDelay(retryInterval)
                .withBackoff(retryInterval, Duration.ofSeconds(10), 1.5D)
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
    public boolean exist(final URI uri) {
        final CheckedSupplier<Boolean> checkedSupplier = () -> super.exist(uri);

        return failsafeExecutor.get(checkedSupplier);
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        final CheckedSupplier<List<String>> checkedSupplier = () -> super.getVersionsByMavenSearch(uri);

        return failsafeExecutor.get(checkedSupplier);
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        final CheckedSupplier<List<String>> checkedSupplier = () -> super.getVersionsByMetaData(uri);

        return failsafeExecutor.get(checkedSupplier);
    }
}
