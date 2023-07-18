// Created: 01.05.2021
package de.freese.maven.proxy.jreserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.ProxyUtils;

/**
 * @author Thomas Freese
 */
class MavenProxyHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyHandler.class);

    private static final String SERVER_NAME = "Maven-Proxy";

    private final Repository repository;

    MavenProxyHandler(final Repository repository) {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}: {}", method, exchange.getRequestURI());

            if (LOGGER.isTraceEnabled()) {
                exchange.getRequestHeaders().forEach((key, value) -> LOGGER.trace("{} = {}", key, value));
            }
        }

        try {
            if ("GET".equals(method)) {
                handleGet(exchange);
            }
            else if ("HEAD".equals(method)) {
                handleHead(exchange);
            }
            //            else if ("PUT".equals(method)) {
            //                handlePut(exchange);
            //            }
            else {
                LOGGER.error("unknown method: {} from {}", method, exchange.getRemoteAddress());

                exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
                exchange.sendResponseHeaders(ProxyUtils.HTTP_SERVICE_UNAVAILABLE, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IOException(ex);
        }
    }

    private void handleGet(final HttpExchange exchange) throws Exception {
        final URI uri = exchange.getRequestURI();

        RepositoryResponse repositoryResponse = this.repository.getInputStream(uri);

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

        boolean exist = this.repository.exist(uri);

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
