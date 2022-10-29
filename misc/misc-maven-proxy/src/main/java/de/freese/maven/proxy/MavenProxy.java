// Created: 01.05.2021
package de.freese.maven.proxy;

import java.util.concurrent.Executor;

import de.freese.maven.proxy.repository.Repository;

/**
 * Interface für einen MavenProxy.
 *
 * @author Thomas Freese
 */
public interface MavenProxy
{
    void setExecutor(final Executor executor);

    void setPort(final int port);

    void setRepository(final Repository repository);

    void start();

    void stop();
}
