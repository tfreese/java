// Created: 05 Apr. 2025
package de.freese.dependency.update.client.apache;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolStats;
import org.jspecify.annotations.Nullable;

import de.freese.dependency.update.client.AbstractRepositoryClient;

/**
 * <a href=https://github.com/apache/httpcomponents-client/blob/5.4.x/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientConfiguration.java>config</a>
 *
 * @author Thomas Freese
 */
final class ApacheHttpRepositoryClient extends AbstractRepositoryClient {

    /**
     * Use full URL with Protocol and Host.
     *
     * @see RequestLine
     */
    private static String toString(final HttpRequest request) {
        // request.getRequestUri();
        // new RequestLine(request)

        try {
            return "%s %s %s".formatted(
                    request.getMethod(),
                    request.getUri().toString(),
                    request.getVersion() != null ? request.getVersion() : HttpVersion.HTTP_1_1);
        }
        catch (final URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Use full URL with Protocol and Host.
     *
     * @see StatusLine
     */
    private static String toString(final HttpRequest request, final HttpResponse response) {
        // request.getRequestUri();
        // new RequestLine(request)
        // new StatusLine(response)

        try {
            if (response.getReasonPhrase() == null) {
                return "%s %s %s %d".formatted(request.getMethod(),
                        request.getUri().toString(),
                        response.getVersion() != null ? response.getVersion() : HttpVersion.HTTP_1_1,
                        response.getCode());
            } else {
                return "%s %s %s %d %s".formatted(request.getMethod(),
                        request.getUri().toString(),
                        response.getVersion() != null ? response.getVersion() : HttpVersion.HTTP_1_1,
                        response.getCode(),
                        response.getReasonPhrase());
            }
        }
        catch (final URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final CloseableHttpClient httpClient;
    private final Supplier<PoolStats> poolStatsSupplier;

    ApacheHttpRepositoryClient(final CloseableHttpClient httpClient, final @Nullable Supplier<PoolStats> poolStatsSupplier) {
        super();

        this.httpClient = Objects.requireNonNull(httpClient, "httpClient required");
        this.poolStatsSupplier = Objects.requireNonNull(poolStatsSupplier, "poolStatsSupplier required");
    }

    @Override
    public void close() {
        getLogger().info("close");

        Optional.ofNullable(poolStatsSupplier)
                .map(Supplier::get)
                .ifPresent(poolStats -> getLogger().info("Connections: {}", poolStats));

        httpClient.close(CloseMode.GRACEFUL);
    }

    @Override
    public boolean exist(final URI uri) {
        final ClassicHttpRequest request = ClassicRequestBuilder
                .head(uri)
                .build();

        try {
            return httpClient.execute(request, response -> {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("{}", toString(request, response));
                }

                if (response.getCode() == HttpStatus.SC_OK) {
                    return true;
                }

                if (response.getCode() == HttpStatus.SC_NOT_FOUND) {
                    return false;
                }

                getLogger().warn("Response {}: {}", response.getCode(), uri);

                return false;
            });
        }
        catch (final Exception ex) {
            getLogger().error("{}", toString(request));
            getLogger().error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        final ClassicHttpRequest request = ClassicRequestBuilder
                .get(uri)
                .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .build();

        try {
            return httpClient.execute(request, response -> {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("{}", toString(request, response));
                }

                if (response.getCode() == HttpStatus.SC_OK) {
                    final String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                    return parseVersionsJson(json);
                }

                getLogger().warn("Response {}: {}", response.getCode(), uri);

                return List.of();
            });
        }
        catch (final Exception ex) {
            getLogger().error("{}", toString(request));
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        final ClassicHttpRequest request = ClassicRequestBuilder
                .get(uri)
                .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType())
                .build();

        try {
            return httpClient.execute(request, response -> {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("{}", toString(request, response));
                }

                if (response.getCode() == HttpStatus.SC_OK) {
                    try (InputStream inputStream = response.getEntity().getContent()) {
                        return parseVersionsXml(inputStream);
                    }
                    catch (final Exception ex) {
                        getLogger().error(ex.getMessage(), ex);
                    }
                }

                getLogger().warn("Response {}: {}", response.getCode(), uri);

                return List.of();
            });
        }
        catch (final Exception ex) {
            getLogger().error("{}", toString(request));
            getLogger().error(ex.getMessage(), ex);
        }

        return List.of();
    }
}
