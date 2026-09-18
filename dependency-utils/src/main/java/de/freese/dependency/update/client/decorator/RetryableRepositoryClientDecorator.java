package de.freese.dependency.update.client.decorator;

import java.net.HttpRetryException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.RetryPolicy;
import dev.failsafe.function.CheckedSupplier;

import de.freese.dependency.update.client.RepositoryClient;

/**
 * @author Thomas Freese
 */
public final class RetryableRepositoryClientDecorator extends AbstractRepositoryClientDecorator {
    private final FailsafeExecutor<Object> failsafeExecutor;

    public RetryableRepositoryClientDecorator(final RepositoryClient delegate, final int maxRetries, final Duration retryInterval) {
        super(delegate);

        final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                // .withDelay(retryInterval)
                // .handle(RepositoryClientException.class)
                .withMaxRetries(maxRetries)
                .withBackoff(retryInterval, Duration.ofSeconds(10), 1.5D)
                .onRetry(event -> {
                    final Throwable lastException = event.getLastException();

                    if (lastException instanceof final HttpRetryException httpRetryException) {
                        getLogger().warn("onRetry: {} - HTTP {} - {} - {}",
                                event.getExecutionCount(),
                                httpRetryException.responseCode(),
                                httpRetryException.getMessage(),
                                httpRetryException.getLocation()
                        );
                    }
                    else if (lastException != null) {
                        final String error = Optional.ofNullable(lastException.getMessage()).orElse(lastException.getClass().getSimpleName());
                        getLogger().warn("onRetry: {} - {}", event.getExecutionCount(), error);
                    }
                    else {
                        getLogger().warn("onRetry: {}", event.getExecutionCount());
                    }
                })
                .onFailure(event ->
                        getLogger().error("onFailure: {}", Optional.ofNullable(event.getException()).map(Throwable::getMessage).orElse(event.toString()))
                )
                .build();

        failsafeExecutor = Failsafe.with(retryPolicy);

        // FailsafeExecutor<Object> failsafeExecutorWithBooleanDefault= Failsafe.with(Fallback.of(false), retryPolicy);
        // FailsafeExecutor<Object> failsafeExecutorWithListDefault=  Failsafe.with(Fallback.of(List.of()), retryPolicy);
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
