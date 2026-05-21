package de.freese.dependency.update.client.jakarta;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.apache.hc.core5.pool.PoolStats;
import org.glassfish.jersey.apache5.connector.Apache5ClientProperties;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.apache5.connector.Apache5HttpClientBuilderConfigurator;
import org.glassfish.jersey.client.ClientConfig;

import de.freese.dependency.update.client.RepositoryClient;
import de.freese.dependency.update.client.apache.ApacheHttpRepositoryClientBuilder;

/**
 * @author Thomas Freese
 */
public final class JakartaRepositoryClientBuilder extends ApacheHttpRepositoryClientBuilder {
    @Override
    public RepositoryClient build() throws Exception {
        final AtomicReference<Supplier<PoolStats>> poolStatsReference = new AtomicReference<>(null);

        final ClientConfig clientConfig = new ClientConfig()
                // .connectorProvider(new JavaNetHttpConnectorProvider());
                .connectorProvider(new Apache5ConnectorProvider())
                .property(Apache5ClientProperties.CONNECTION_MANAGER, createHttpClientConnectionManager(poolStatsReference))
                .property(Apache5ClientProperties.KEEPALIVE_STRATEGY, createConnectionKeepAliveStrategy())
                .property(Apache5ClientProperties.REQUEST_CONFIG, createRequestConfig())
                .property(Apache5ClientProperties.RETRY_STRATEGY, createHttpRequestRetryStrategy())
                .property(Apache5ClientProperties.REUSE_STRATEGY, createConnectionReuseStrategy())
                .register((Apache5HttpClientBuilderConfigurator) this::configHttpClientBuilder);

        return new JakartaRepositoryClient(clientConfig, poolStatsReference.get());
    }

    @Override
    protected JakartaRepositoryClientBuilder self() {
        return this;
    }
}
