// Created: 19.07.23
package de.freese.maven.proxy.repository;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public class EmptyRepository implements Repository {
    @Override
    public boolean exist(final URI resource) throws Exception {
        return false;
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        //        return new RepositoryResponse(resource, 0, InputStream.nullInputStream());
        return null;
    }
}
