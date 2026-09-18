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
import de.freese.dependency.update.client.RepositoryClientException;

/**
 * @author Thomas Freese
 * @since 03.04.2025
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

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("HEAD {} {}", uri, response.statusCode());
            }

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }

            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", response.statusCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for exist-Request " + response.statusCode() + " for " + uri);
        }
        catch (final InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            throw new RepositoryClientException(ex);
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
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

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("GET {} {}", uri, response.statusCode());
            }

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return parseVersionsJson(response.body());
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", response.statusCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for MavenSearch-Request " + response.statusCode() + " for " + uri);
        }
        catch (final InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            throw new RepositoryClientException(ex);
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
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

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("GET {} {}", uri, response.statusCode());
            }

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = response.body()) {
                    return parseVersionsXml(inputStream);
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", response.statusCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for MetaData-Request " + response.statusCode() + " for " + uri);
        }
        catch (final InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            throw new RepositoryClientException(ex);
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
    }
}
