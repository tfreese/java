// Created: 03 Apr. 2025
package de.freese.dependency.update.client.jnh;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

import de.freese.dependency.update.client.AbstractRepositoryClient;

/**
 * @author Thomas Freese
 */
final class JreHttpRepositoryClient extends AbstractRepositoryClient {
    private final HttpClient httpClient;

    JreHttpRepositoryClient(final HttpClient httpClient) {
        super();

        this.httpClient = Objects.requireNonNull(httpClient, "httpClient required");
    }

    @Override
    public void close() {
        getLogger().info("close");

        httpClient.close();
    }

    @Override
    public boolean exist(final URI uri) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .HEAD()
                .build();

        try {
            final HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            getLogger().debug("HEAD {} {}", uri, response.statusCode());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }

            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }

            getLogger().warn("Response {}: {}", response.statusCode(), uri);
        }
        catch (final InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            getLogger().debug("GET {} {}", uri, response.statusCode());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return parseVersionsJson(response.body());
            }

            getLogger().warn("Response {}: {}", response.statusCode(), uri);
        }
        catch (final InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Accept", "application/xml")
                .build();

        try {
            final HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            // final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            getLogger().debug("GET {} {}", uri, response.statusCode());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = response.body()) {
                    return parseVersionsXml(inputStream);
                }
            }

            getLogger().warn("Response {}: {}", response.statusCode(), uri);
        }
        catch (final InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

}
