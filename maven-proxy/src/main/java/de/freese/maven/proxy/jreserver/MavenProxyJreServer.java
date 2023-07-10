// Created: 01.05.2021
package de.freese.maven.proxy.jreserver;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.repository.Repository;

/**
 * @author Thomas Freese
 */
public class MavenProxyJreServer implements MavenProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyJreServer.class);

    private Executor executor;

    private HttpServer httpServer;

    private int port = -1;

    private Repository repository;

    @Override
    public void setExecutor(final Executor executor) {
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    @Override
    public void setPort(final int port) {
        if (port <= 0) {
            throw new IllegalArgumentException("port <= 0");
        }

        this.port = port;
    }

    @Override
    public void setRepository(final Repository repository) {
        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    @Override
    public void start() {
        Objects.requireNonNull(this.repository, "repository required");
        Objects.requireNonNull(this.executor, "executor required");

        if (this.port <= 0) {
            throw new IllegalArgumentException("port <= 0");
        }

        LOGGER.info("Starting MavenProxy at Port {}", this.port);

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
            this.httpServer.setExecutor(this.executor);
            this.httpServer.createContext("/", new MavenProxyHandler(this.repository));
            this.httpServer.start();
        }
        catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);

            throw ex;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);

            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping MavenProxy at Port {}", this.port);

        this.httpServer.stop(3);
    }
}
