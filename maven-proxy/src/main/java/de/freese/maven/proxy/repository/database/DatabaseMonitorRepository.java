// Created: 10.07.23
package de.freese.maven.proxy.repository.database;

import java.net.URI;
import java.util.Objects;

import javax.sql.DataSource;

import de.freese.maven.proxy.repository.AbstractRepository;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.RepositoryResponse;

/**
 * Monitor last access of a resource to handle deletion tasks.
 *
 * @author Thomas Freese
 */
public class DatabaseMonitorRepository extends AbstractRepository {

    private final DataSource dataSource;

    private final Repository delegate;

    public DatabaseMonitorRepository(final Repository delegate, DataSource dataSource) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        boolean exist = this.delegate.exist(resource);

        //        if (exist) {
        //            // TODO Monitor last access.
        //        }

        return exist;
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        // TODO Monitor last access.
        return this.delegate.getInputStream(resource);
    }
}
