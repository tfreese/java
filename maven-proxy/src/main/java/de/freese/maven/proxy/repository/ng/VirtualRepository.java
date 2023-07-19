// Created: 19.07.23
package de.freese.maven.proxy.repository.ng;

import java.util.List;

/**
 * @author Thomas Freese
 */
public interface VirtualRepository extends Repository {

    void add(RemoteRepository repository);

    void add(LocalRepository repository);

    List<Repository> getRepositories();
}
