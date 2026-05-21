package de.freese.dependency.update.client;

import java.time.Duration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractRepositoryHttpClientBuilder<B> {
    protected static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(30L);
    protected static final int DEFAULT_MAX_RETRIES = 3;
    protected static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30L);
    protected static final Duration DEFAULT_RETRY_INTERVAL = Duration.ofSeconds(3L);

    private Duration connectTimeout;
    private HostnameVerifier hostnameVerifier;
    private int maxRetries;
    private Duration readTimeout;
    private Duration retryInterval;
    private SSLContext sslContext;

    public abstract RepositoryClient build() throws Exception;

    public B connectTimeout(final Duration connectTimeout) {
        this.connectTimeout = connectTimeout;

        return self();
    }

    public B hostnameVerifier(final HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;

        return self();
    }

    public B maxRetries(final int maxRetries) {
        if (maxRetries < 1) {
            throw new IllegalArgumentException("maxRetries must be a positive integer");
        }

        this.maxRetries = maxRetries;

        return self();
    }

    public B readTimeout(final Duration readTimeout) {
        this.readTimeout = readTimeout;

        return self();
    }

    public B retryInterval(final Duration retryInterval) {
        this.retryInterval = retryInterval;

        return self();
    }

    public B sslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;

        return self();
    }

    protected Duration getConnectTimeout() {
        return connectTimeout;
    }

    protected HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    protected int getMaxRetries() {
        return maxRetries;
    }

    protected Duration getReadTimeout() {
        return readTimeout;
    }

    protected Duration getRetryInterval() {
        return retryInterval;
    }

    protected SSLContext getSslContext() {
        return sslContext;
    }

    protected abstract B self();
}
