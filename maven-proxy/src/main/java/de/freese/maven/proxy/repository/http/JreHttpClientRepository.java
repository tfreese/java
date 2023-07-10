// Created: 28.12.2011
package de.freese.maven.proxy.repository.http;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.ProxyUtils;

/**
 * @author Thomas Freese
 */
public class JreHttpClientRepository extends AbstractHttpRepository {
    private final HttpClient httpClient;

    public JreHttpClientRepository(final HttpClient httpClient, final String uri) {
        this(httpClient, URI.create(uri));
    }

    public JreHttpClientRepository(final HttpClient httpClient, final URI uri) {
        super(uri);

        this.httpClient = Objects.requireNonNull(httpClient, "httpClient required");
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        URI uri = createResourceUri(getBaseUri(), resource);

        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(ProxyUtils.HTTP_HEADER_USER_AGENT, "Maven-Proxy")
                .method("HEAD", BodyPublishers.noBody())
                .build()
                ;
        // @formatter:on

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(request.toString());
        }

        HttpResponse<Void> response = this.httpClient.send(request, BodyHandlers.discarding());

        return response.statusCode() == ProxyUtils.HTTP_OK;
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        URI uri = createResourceUri(getBaseUri(), resource);

        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(ProxyUtils.HTTP_HEADER_USER_AGENT, "Maven-Proxy")
                .GET()
                .build()
                ;
        // @formatter:on

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(request.toString());
        }

        HttpResponse<InputStream> response = this.httpClient.send(request, BodyHandlers.ofInputStream());

        if (response.statusCode() != ProxyUtils.HTTP_OK) {
            return null;
        }

        long contentLength = response.headers().firstValueAsLong(ProxyUtils.HTTP_HEADER_CONTENT_LENGTH).orElse(0);

        return new RepositoryResponse(uri, contentLength, response.body());
    }
}
