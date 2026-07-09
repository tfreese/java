// Created: 03 Apr. 2025
package de.freese.dependency.update.client.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import javax.net.ssl.HttpsURLConnection;

import de.freese.dependency.update.client.AbstractRepositoryClient;

/**
 * @author Thomas Freese
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

            getLogger().debug("HEAD {} {}", uri, connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }

            getLogger().warn("Response {}: {}", connection.getResponseCode(), uri);
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        try {
            final HttpsURLConnection connection = createConnection(uri);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            connection.connect();

            getLogger().debug("GET {} {}", uri, connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    final String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    return parseVersionsJson(json);
                }
            }

            getLogger().warn("Response {}: {}", connection.getResponseCode(), uri);
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        try {
            final HttpsURLConnection connection = createConnection(uri);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            connection.connect();

            getLogger().debug("GET {} {}", uri, connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    return parseVersionsXml(inputStream);
                }
            }

            getLogger().warn("Response {}: {}", connection.getResponseCode(), uri);
        }
        catch (final Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

    private HttpsURLConnection createConnection(final URI uri) throws IOException {
        return connectionConfigurer.apply((HttpsURLConnection) uri.toURL().openConnection());
    }
}
