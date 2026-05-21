package de.freese.dependency.update.client.url;

import java.net.Authenticator;
import java.time.Duration;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import de.freese.dependency.update.client.AbstractRepositoryHttpClientBuilder;
import de.freese.dependency.update.client.RepositoryClient;

/**
 * @author Thomas Freese
 */
public final class UrlConnectionRepositoryClientBuilder extends AbstractRepositoryHttpClientBuilder<UrlConnectionRepositoryClientBuilder> {
    private Authenticator authenticator;

    public UrlConnectionRepositoryClientBuilder authenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;

        return self();
    }

    @Override
    public RepositoryClient build() throws Exception {
        final int maxRetries = getMaxRetries() == 0 ? DEFAULT_MAX_RETRIES : getMaxRetries();
        final Duration retryInterval = Objects.requireNonNullElse(getRetryInterval(), DEFAULT_RETRY_INTERVAL);
        final Duration connectTimeout = Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT);
        final Duration readTimeout = Objects.requireNonNullElse(getReadTimeout(), DEFAULT_READ_TIMEOUT);
        final SSLContext sslContext = Objects.requireNonNullElse(getSslContext(), SSLContext.getDefault());
        final HostnameVerifier hostnameVerifier = Objects.requireNonNullElse(getHostnameVerifier(), HttpsURLConnection.getDefaultHostnameVerifier());

        return new UrlConnectionRepositoryClient(maxRetries, retryInterval, connection -> {
            connection.setConnectTimeout((int) connectTimeout.toMillis());
            connection.setReadTimeout((int) readTimeout.toMillis());
            connection.setDoOutput(true); // Write to Connection (getOutputStream).
            connection.setDoInput(true); // Read from Connection (getInputStream).
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);

            if (authenticator != null) {
                connection.setAuthenticator(authenticator);
            }

            return connection;
        });
    }

    @Override
    protected UrlConnectionRepositoryClientBuilder self() {
        return this;
    }
}
