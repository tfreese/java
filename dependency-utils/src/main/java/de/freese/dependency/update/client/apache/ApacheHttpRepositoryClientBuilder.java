package de.freese.dependency.update.client.apache;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.net.ssl.HttpsURLConnection;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import de.freese.dependency.update.client.AbstractRepositoryHttpClientBuilder;
import de.freese.dependency.update.client.RepositoryClient;

/**
 * <a href=https://github.com/apache/httpcomponents-client/blob/master/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientConfiguration.java>config</a>
 *
 * @author Thomas Freese
 */
public class ApacheHttpRepositoryClientBuilder extends AbstractRepositoryHttpClientBuilder<ApacheHttpRepositoryClientBuilder> {
    private static final int DEFAULT_BUFFER_SIZE = 1_048_576;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int DEFAULT_CHUNK_SIZE = 1_048_576;
    private static final Duration DEFAULT_EVICT_IDLE = Duration.ofMinutes(15L);
    private static final int DEFAULT_MAX_CONN = 20;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10L);

    private int bufferSize;
    private UnaryOperator<HttpClientBuilder> builderConfigurer;
    private Charset charset;
    private int chunkSize;
    private Duration evictIdleConnections;
    private int maxConnTotal;
    private AtomicReference<Supplier<PoolStats>> poolStatsReference;
    private Duration timeToLive;

    public ApacheHttpRepositoryClientBuilder bufferSize(final int bufferSize) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("bufferSize must be a positive integer");
        }

        this.bufferSize = bufferSize;

        return self();
    }

    @Override
    public RepositoryClient build() throws Exception {
        if (poolStatsReference == null) {
            poolStatsReference = new AtomicReference<>(null);
        }

        final HttpClientConnectionManager connectionManager = createHttpClientConnectionManager(poolStatsReference);
        final RequestConfig requestConfig = createRequestConfig();
        final ConnectionKeepAliveStrategy connectionKeepAliveStrategy = createConnectionKeepAliveStrategy();
        final ConnectionReuseStrategy connectionReuseStrategy = createConnectionReuseStrategy();
        final HttpRequestRetryStrategy httpRequestRetryStrategy = createHttpRequestRetryStrategy();

        // final HttpHost targetHost = new HttpHost("https", "example.com", 443);
        //
        // 1st Request sends without Auth-Data, if 401 the 2nd Request is sent with Auth-Data (1 extra Round-Trip).
        // final CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
        //         .add(new HttpHost("https", host, 443), new UsernamePasswordCredentials(user, token))
        //         // .add(new HttpHost("https", host, 443) , new BearerToken(token)
        //         .build();
        //
        // // Preemptive Auth: Send Auth for every Request.
        // final AuthCache authCache = new BasicAuthCache();
        // authCache.put(targetHost, new BearerScheme());
        //
        // ClientContext is not Thread-Safe, using new one for each Request.
        // final HttpClientContext clientContext = ContextBuilder.create()
        //         .useCredentialsProvider(credentialsProvider)
        //         // .preemptiveAuth(targetHost, new BearerScheme())
        //         // .preemptiveBasicAuth(targetHost, new UsernamePasswordCredentials(user, token()))
        //         .useAuthCache(authCache)
        //         .build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setConnectionReuseStrategy(connectionReuseStrategy)
                .setRetryStrategy(httpRequestRetryStrategy);

        httpClientBuilder = configHttpClientBuilder(httpClientBuilder);

        return new ApacheHttpRepositoryClient(httpClientBuilder.build(), poolStatsReference.get());
    }

    public ApacheHttpRepositoryClientBuilder builderConfigurer(final UnaryOperator<HttpClientBuilder> builderConfigurer) {
        this.builderConfigurer = builderConfigurer;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder charset(final Charset charset) {
        this.charset = charset;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder chunkSize(final int chunkSize) {
        if (chunkSize < 1) {
            throw new IllegalArgumentException("chunkSize must be a positive integer");
        }

        this.chunkSize = chunkSize;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder evictIdleConnections(final Duration evictIdleConnections) {
        this.evictIdleConnections = evictIdleConnections;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder maxConnTotal(final int maxConnTotal) {
        if (maxConnTotal < 1) {
            throw new IllegalArgumentException("maxConnTotal must be a positive integer");
        }

        this.maxConnTotal = maxConnTotal;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder poolStatsReference(final AtomicReference<Supplier<PoolStats>> poolStatsReference) {
        this.poolStatsReference = poolStatsReference;

        return self();
    }

    public ApacheHttpRepositoryClientBuilder timeToLive(final Duration timeToLive) {
        this.timeToLive = timeToLive;

        return self();
    }

    protected HttpClientBuilder configHttpClientBuilder(final HttpClientBuilder httpClientBuilder) {
        httpClientBuilder
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.of(Objects.requireNonNullElse(evictIdleConnections, DEFAULT_EVICT_IDLE)))
                .setUserAgent("Java Application Client")
        ;

        if (builderConfigurer != null) {
            builderConfigurer.apply(httpClientBuilder);
        }

        return httpClientBuilder;
    }

    protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy() {
            @Override
            public TimeValue getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                final TimeValue duration = super.getKeepAliveDuration(response, context);

                return duration.getDuration() == -1L ? TimeValue.ofMinutes(2L) : duration;
            }
        };
    }

    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        return new DefaultConnectionReuseStrategy();
    }

    protected HttpClientConnectionManager createHttpClientConnectionManager(final AtomicReference<Supplier<PoolStats>> poolStatsReference) {
        final HttpConnectionFactory<ManagedHttpClientConnection> connectionSocketFactory = createHttpConnectionFactory();

        // new NoopHostnameVerifier()
        final TlsSocketStrategy tlsSocketStrategy;

        if (getSslContext() != null) {
            tlsSocketStrategy = new DefaultClientTlsStrategy(getSslContext(), Objects.requireNonNullElse(getHostnameVerifier(), HttpsURLConnection.getDefaultHostnameVerifier()));
        } else {
            tlsSocketStrategy = DefaultClientTlsStrategy.createSystemDefault();
        }

        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setConnectionFactory(connectionSocketFactory)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.of(Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT)))
                        .setSocketTimeout(Timeout.of(Objects.requireNonNullElse(getReadTimeout(), DEFAULT_READ_TIMEOUT)))
                        .setTimeToLive(TimeValue.of(Objects.requireNonNullElse(timeToLive, DEFAULT_TTL)))
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.of(Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT)))
                        .build())
                .setTlsSocketStrategy(tlsSocketStrategy)
                .setDefaultTlsConfig(TlsConfig.custom()
                        .setVersionPolicy(HttpVersionPolicy.NEGOTIATE)
                        .setHandshakeTimeout(Timeout.of(Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT)))
                        .setSupportedProtocols(TLS.V_1_3)
                        .build())
                .setMaxConnTotal(maxConnTotal == 0 ? DEFAULT_MAX_CONN : maxConnTotal)
                // .setMaxConnPerRoute((maxConnTotal == 0 ? DEFAULT_MAX_CONN : maxConnTotal) / 2)
                .setConnPoolPolicy(PoolReusePolicy.FIFO)
                .build();

        if (poolStatsReference != null) {
            poolStatsReference.set(poolingHttpClientConnectionManager::getTotalStats);
        }

        return poolingHttpClientConnectionManager;
    }

    protected HttpRequestRetryStrategy createHttpRequestRetryStrategy() {
        return new MyHttpRequestRetryStrategy(getMaxRetries() == 0 ? DEFAULT_MAX_RETRIES : getMaxRetries(),
                TimeValue.of(Objects.requireNonNullElse(getRetryInterval(), DEFAULT_RETRY_INTERVAL)));
    }

    protected RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(Objects.requireNonNullElse(getConnectTimeout(), DEFAULT_CONNECT_TIMEOUT)))
                .setResponseTimeout(Timeout.of(Objects.requireNonNullElse(getReadTimeout(), DEFAULT_READ_TIMEOUT)))
                .setRedirectsEnabled(true)
                .build();
    }

    @Override
    protected ApacheHttpRepositoryClientBuilder self() {
        return this;
    }

    private HttpConnectionFactory<ManagedHttpClientConnection> createHttpConnectionFactory() {
        final Http1Config http1Config = Http1Config.custom()
                .setChunkSizeHint(chunkSize == 0 ? DEFAULT_CHUNK_SIZE : chunkSize)
                .setBufferSize(bufferSize == 0 ? DEFAULT_BUFFER_SIZE : bufferSize)
                .build();

        final CharCodingConfig charCodingConfig = CharCodingConfig.custom()
                .setCharset(Objects.requireNonNullElse(charset, DEFAULT_CHARSET))
                .build();

        return ManagedHttpClientConnectionFactory.builder()
                .http1Config(http1Config)
                .charCodingConfig(charCodingConfig)
                .build();
    }
}
