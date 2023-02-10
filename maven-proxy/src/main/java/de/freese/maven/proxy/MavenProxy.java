// Created: 01.05.2021
package de.freese.maven.proxy;

import java.util.concurrent.Executor;

import de.freese.maven.proxy.repository.Repository;

/**
 * Interface für einen MavenProxy.
 *
 * @author Thomas Freese
 */
public interface MavenProxy {
    void setExecutor(Executor executor);

    void setPort(int port);

    void setRepository(Repository repository);

    void start();

    void stop();
}
