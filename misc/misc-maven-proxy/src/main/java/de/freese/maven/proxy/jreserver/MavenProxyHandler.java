// Created: 01.05.2021
package de.freese.maven.proxy.jreserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.util.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class MavenProxyHandler implements HttpHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyHandler.class);

    private static final String SERVER_NAME = "Maven-Proxy";

    //    private final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private final FileNameMap fileNameMap = URLConnection.getFileNameMap();

    private final Repository repository;

    MavenProxyHandler(final Repository repository)
    {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    /**
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public void handle(final HttpExchange exchange) throws IOException
    {
        String method = exchange.getRequestMethod();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("{}: {}", method, exchange.getRequestURI());
            exchange.getRequestHeaders().forEach((key, value) -> LOGGER.debug("{} = {}", key, value));
        }

        try
        {
            if ("GET".equals(method))
            {
                handleGet(exchange);
            }
            else if ("HEAD".equals(method))
            {
                handleHead(exchange);
            }
            else
            {
                LOGGER.error("unknown method: {}", method);

                exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
                exchange.sendResponseHeaders(ProxyUtils.HTTP_SERVICE_UNAVAILABLE, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage(), ex);
            throw new IOException(ex);
        }
    }

    private void handleGet(final HttpExchange exchange) throws Exception
    {
        final URI uri = exchange.getRequestURI();

        RepositoryResponse repositoryResponse = this.repository.getInputStream(uri);

        if (repositoryResponse == null)
        {
            String message = "File not found: " + uri.toString();
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
            exchange.sendResponseHeaders(ProxyUtils.HTTP_NOT_FOUND, bytes.length);

            exchange.getResponseBody().write(bytes);

            return;
        }

        long fileLength = repositoryResponse.getContentLength();

        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
        //        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_CONTENT_TYPE, this.mimeTypesMap.getContentType(repositoryResponse.getFileName()));
        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_CONTENT_TYPE, fileNameMap.getContentTypeFor(repositoryResponse.getFileName()));

        exchange.sendResponseHeaders(ProxyUtils.HTTP_OK, fileLength);

        try (OutputStream outputStream = exchange.getResponseBody())
        {
            repositoryResponse.transferTo(outputStream);

            outputStream.flush();
        }
    }

    private void handleHead(final HttpExchange exchange) throws Exception
    {
        final URI uri = exchange.getRequestURI();

        boolean exist = this.repository.exist(uri);

        int response = exist ? ProxyUtils.HTTP_OK : ProxyUtils.HTTP_NOT_FOUND;

        exchange.getResponseHeaders().add(ProxyUtils.HTTP_HEADER_SERVER, SERVER_NAME);
        exchange.sendResponseHeaders(response, -1);
    }
}
