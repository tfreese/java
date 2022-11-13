// Created: 30.04.2021
package de.freese.maven.proxy;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Thomas Freese
 */
public final class HttpDumpServerMain
{
    public static void main(final String[] args) throws Exception
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);
        // server.setExecutor(Executors.newSingleThreadExecutor());
        // server.setExecutor(null);

        server.createContext("/", httpExchange ->
        {

            System.out.println();
            // System.out.println(Thread.currentThread().getName());
            System.out.printf("%s: %s%n", httpExchange.getRequestMethod(), httpExchange.getRequestURI().toString());
            httpExchange.getRequestHeaders().entrySet().forEach(System.out::println);

            httpExchange.sendResponseHeaders(HttpResponseStatus.SERVICE_UNAVAILABLE.code(), 0);
            httpExchange.getResponseBody().close();
        });

        new Thread(server::start).start();
    }

    private HttpDumpServerMain()
    {
        super();
    }
}
