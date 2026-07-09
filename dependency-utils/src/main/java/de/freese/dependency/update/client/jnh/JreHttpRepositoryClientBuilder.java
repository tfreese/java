package de.freese.dependency.update.client.jnh;

import java.net.Authenticator;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.jspecify.annotations.Nullable;

import de.freese.dependency.update.client.AbstractRepositoryHttpClientBuilder;
import de.freese.dependency.update.client.RepositoryClient;
import de.freese.dependency.update.client.RetryableRepositoryClient;

/**
 * @author Thomas Freese
 */
public final class JreHttpRepositoryClientBuilder extends AbstractRepositoryHttpClientBuilder<JreHttpRepositoryClientBuilder> {
    @Nullable
    private Authenticator authenticator;

    public JreHttpRepositoryClientBuilder authenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;

        return self();
    }

    @Override
    public RepositoryClient build() throws Exception {
        final int maxRetries = getMaxRetries() == 0 ? DEFAULT_MAX_RETRIES : getMaxRetries();

        final Duration retryInterval = Objects.requireNonNullElse(getRetryInterval(), DEFAULT_RETRY_INTERVAL);
        final Duration connectTimeout = Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT);
        final SSLContext sslContext = Objects.requireNonNullElse(getSslContext(), SSLContext.getDefault());

        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(connectTimeout)
                .sslContext(sslContext)
                .followRedirects(HttpClient.Redirect.ALWAYS);

        if (authenticator != null) {
            httpClientBuilder = httpClientBuilder.authenticator(authenticator);
        }

        final RepositoryClient repositoryClient = new JreHttpRepositoryClient(httpClientBuilder.build());

        return new RetryableRepositoryClient(repositoryClient, maxRetries, retryInterval);
    }

    @Override
    protected JreHttpRepositoryClientBuilder self() {
        return this;
    }
}
