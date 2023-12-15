// Created: 13.09.2020
package de.freese.sonstiges.server;

import java.nio.channels.SelectionKey;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.server.handler.IoHandler;

/**
 * @author Thomas Freese
 */
public abstract class AbstractServer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int port;
    /**
     * ReentrantLock nicht möglich, da dort die Locks auf Thread-Ebene verwaltet werden.
     */
    private final Semaphore startLock = new Semaphore(1, true);

    private IoHandler<SelectionKey> ioHandler;
    private String name = getClass().getSimpleName();

    protected AbstractServer(final int port) {
        super();

        if (port <= 0) {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        this.port = port;

        this.startLock.acquireUninterruptibly();
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isStarted() {
        return getStartLock().availablePermits() > 0;
    }

    public void setIoHandler(final IoHandler<SelectionKey> ioHandler) {
        this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * Starten des Servers.
     */
    public abstract void start();

    /**
     * Stoppen des Servers.
     */
    public abstract void stop();

    protected IoHandler<SelectionKey> getIoHandler() {
        return this.ioHandler;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected Semaphore getStartLock() {
        return this.startLock;
    }
}
