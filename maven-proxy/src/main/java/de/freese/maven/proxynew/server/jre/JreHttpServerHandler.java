// Created: 22.07.23
package de.freese.maven.proxynew.server.jre;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.AbstractComponent;
import de.freese.maven.proxy.utils.HttpMethod;
import de.freese.maven.proxy.utils.ProxyUtils;
import de.freese.maven.proxynew.repository.Repository;

/**
 * @author Thomas Freese
 */
public class JreHttpServerHandler extends AbstractComponent implements HttpHandler {

    private static final String SERVER_NAME = "Maven-Proxy";

    private final Repository repository;

    JreHttpServerHandler(final Repository repository) {
        this.repository = checkNotNull(repository, "Repository");
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.get(exchange.getRequestMethod());

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("{}: {}", httpMethod, exchange.getRequestURI());

            if (getLogger().isTraceEnabled()) {
                exchange.getRequestHeaders().forEach((key, value) -> getLogger().trace("{} = {}", key, value));
            }
        }

        if (!getRepository().supports(httpMethod)) {
            getLogger().error("Repository does not support HttpMethod: {} -{}", getRepository().getName(), httpMethod);

            exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
            exchange.sendResponseHeaders(ProxyUtils.HTTP_SERVICE_UNAVAILABLE, 0);
            exchange.getResponseBody().close();

            return;
        }

        try {
            if (HttpMethod.GET.equals(httpMethod)) {
                handleGet(exchange);
            }
            else if (HttpMethod.HEAD.equals(httpMethod)) {
                handleHead(exchange);
            }
            else if (HttpMethod.PUT.equals(httpMethod)) {
                handlePut(exchange);
            }
            else {
                getLogger().error("unknown method: {} from {}", httpMethod, exchange.getRemoteAddress());

                exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
                exchange.sendResponseHeaders(ProxyUtils.HTTP_SERVICE_UNAVAILABLE, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
            throw new IOException(ex);
        }
    }

    protected Repository getRepository() {
        return repository;
    }

    private void handleGet(final HttpExchange exchange) throws Exception {
        final URI uri = exchange.getRequestURI();

        RepositoryResponse repositoryResponse = getRepository().getInputStream(uri);

        if (repositoryResponse == null) {
            String message = "File not found: " + uri.toString();
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(ProxyUtils.HTTP_NOT_FOUND, bytes.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                exchange.getResponseBody().write(bytes);

                outputStream.flush();
            }

            return;
        }

        long fileLength = repositoryResponse.getContentLength();

        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_CONTENT_TYPE, ProxyUtils.getContentType(repositoryResponse.getFileName()));
        exchange.sendResponseHeaders(ProxyUtils.HTTP_OK, fileLength);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            repositoryResponse.transferTo(outputStream);

            outputStream.flush();
        }
    }

    private void handleHead(final HttpExchange exchange) throws Exception {
        final URI uri = exchange.getRequestURI();

        boolean exist = getRepository().exist(uri);

        int response = exist ? ProxyUtils.HTTP_OK : ProxyUtils.HTTP_NOT_FOUND;

        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
        exchange.sendResponseHeaders(response, -1);
    }

    /**
     * Deploy
     **/
    private void handlePut(final HttpExchange exchange) throws Exception {
        final URI uri = exchange.getRequestURI();

        String pathString = uri.getPath().substring(1);
        Path path = Paths.get("/tmp", pathString);

        Files.createDirectories(path.getParent());

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            exchange.getRequestBody().transferTo(outputStream);
        }

        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
        exchange.sendResponseHeaders(ProxyUtils.HTTP_OK, -1);
    }
}
