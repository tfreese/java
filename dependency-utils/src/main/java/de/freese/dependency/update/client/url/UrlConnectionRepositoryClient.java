package de.freese.dependency.update.client.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import javax.net.ssl.HttpsURLConnection;

import de.freese.dependency.update.client.AbstractRepositoryClient;
import de.freese.dependency.update.client.RepositoryClientException;

/**
 * @author Thomas Freese
 * @since 03.04.2025
 */
final class UrlConnectionRepositoryClient extends AbstractRepositoryClient {

    private final UnaryOperator<HttpsURLConnection> connectionConfigurer;

    UrlConnectionRepositoryClient(final UnaryOperator<HttpsURLConnection> connectionConfigurer) {
        super();

        this.connectionConfigurer = Objects.requireNonNull(connectionConfigurer, "connectionConfigurer required");
    }

    @Override
    public void close() {
        getLogger().info("close");
    }

    @Override
    public boolean exist(final URI uri) {
        try {
            final HttpsURLConnection connection = createConnection(uri);
            connection.setRequestMethod("HEAD");

            connection.connect();

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("HEAD {} {}", uri, connection.getResponseCode());
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", connection.getResponseCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for exist-Request " + connection.getResponseCode() + " for " + uri);
        }
        catch (final RepositoryClientException ex) {
            throw ex;
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        try {
            final HttpsURLConnection connection = createConnection(uri);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            connection.connect();

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("GET {} {}", uri, connection.getResponseCode());
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    // final String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    // return parseVersionsJson(json);
                    return parseVersionsJson(inputStream);
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", connection.getResponseCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for MavenSearch-Request " + connection.getResponseCode() + " for " + uri);
        }
        catch (final RepositoryClientException ex) {
            throw ex;
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        try {
            final HttpsURLConnection connection = createConnection(uri);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            connection.connect();

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("GET {} {}", uri, connection.getResponseCode());
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    return parseVersionsXml(inputStream);
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Response {}: {}", connection.getResponseCode(), uri);
            }

            throw new RepositoryClientException("Unexpected response status for MetaData-Request " + connection.getResponseCode() + " for " + uri);
        }
        catch (final RepositoryClientException ex) {
            throw ex;
        }
        catch (final Exception ex) {
            throw new RepositoryClientException(ex);
        }
    }

    private HttpsURLConnection createConnection(final URI uri) throws IOException {
        return connectionConfigurer.apply((HttpsURLConnection) uri.toURL().openConnection());
    }
}
