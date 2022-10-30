// Created: 18.09.2019
package de.freese.maven.proxy.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper für mehrere {@link Repository}.
 *
 * @author Thomas Freese
 */
public class CompositeRepository extends AbstractRepository
{
    private final List<Repository> repositories = new ArrayList<>();

    public void addRepository(final Repository repository)
    {
        this.repositories.add(Objects.requireNonNull(repository, "repository required"));
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(java.net.URI)
     */
    @Override
    public boolean exist(final URI resource) throws Exception
    {
        boolean exist = false;

        for (Repository remoteRepository : this.repositories)
        {
            try
            {
                exist = remoteRepository.exist(resource);
            }
            catch (Exception ex)
            {
                getLogger().warn("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            }

            if (exist)
            {
                break;
            }
        }

        return exist;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getInputStream(java.net.URI)
     */
    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception
    {
        RepositoryResponse response = null;

        for (Repository remoteRepository : this.repositories)
        {
            try
            {
                response = remoteRepository.getInputStream(resource);
            }
            catch (Exception ex)
            {
                getLogger().warn("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            }

            if (response != null)
            {
                break;
            }
        }

        return response;
    }

    /**
     * @see de.freese.maven.proxy.repository.AbstractRepository#toString()
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
