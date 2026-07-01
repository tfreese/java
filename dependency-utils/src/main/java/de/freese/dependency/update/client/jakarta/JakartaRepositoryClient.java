// Created: 18 Apr. 2025
package de.freese.dependency.update.client.jakarta;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.hc.core5.pool.PoolStats;
import org.glassfish.jersey.client.ClientConfig;

import de.freese.dependency.update.client.AbstractRepositoryClient;

/**
 * @author Thomas Freese
 */
final class JakartaRepositoryClient extends AbstractRepositoryClient {
    private final Client client;
    private final Supplier<PoolStats> poolStatsSupplier;

    JakartaRepositoryClient(final ClientConfig clientConfig, final Supplier<PoolStats> poolStatsSupplier) {
        super();

        client = ClientBuilder.newBuilder()
                .withConfig(Objects.requireNonNull(clientConfig, "clientConfig required"))
                .build();

        this.poolStatsSupplier = Objects.requireNonNull(poolStatsSupplier, "poolStatsSupplier required");
    }

    @Override
    public void close() {
        getLogger().info("close");

        Optional.ofNullable(poolStatsSupplier)
                .map(Supplier::get)
                .ifPresent(poolStats -> getLogger().info("Connections: {}", poolStats));

        client.close();
    }

    @Override
    public boolean exist(final URI uri) {
        final Invocation.Builder request = client.target(uri).request();

        try (Response response = request.head()) {
            getLogger().debug("HEAD {} {}", uri, response.getStatus());

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return true;
            }

            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return false;
            }

            getLogger().warn("Response {}: {}", response.getStatus(), uri);

            return false;
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        final Invocation.Builder request = client.target(uri).request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        try (Response response = request.get()) {
            getLogger().debug("GET {} {}", uri, response.getStatus());

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String json = response.readEntity(String.class);

                return parseVersionsJson(json);
            }

            getLogger().warn("Response {}: {}", response.getStatus(), uri);

            return List.of();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        getLogger().debug("URI: {}", uri);

        final Invocation.Builder request = client.target(uri).request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);

        try (Response response = request.get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                try (InputStream inputStream = response.readEntity(InputStream.class)) {
                    return parseVersionsXml(inputStream);
                }
            }

            getLogger().warn("Response {}: {}", response.getStatus(), uri);

            return List.of();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }
}
