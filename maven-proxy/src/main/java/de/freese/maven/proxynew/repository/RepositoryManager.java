// Created: 22.07.23
package de.freese.maven.proxynew.repository;

import java.util.Map;
import java.util.TreeMap;

import de.freese.maven.proxy.utils.AbstractComponent;

/**
 * @author Thomas Freese
 */
public class RepositoryManager extends AbstractComponent {

    private final Map<String, Repository> repositories = new TreeMap<>();

    public RepositoryManager add(Repository repository) {
        checkNotNull(repository, "Repository");

        if (repositories.containsKey(repository.getName())) {
            throw new IllegalArgumentException("Repository already exist: " + repository.getName());
        }

        repositories.put(repository.getName(), repository);

        return this;
    }

    public Repository getRepository(String name) {
        return repositories.get(name);
    }
}
