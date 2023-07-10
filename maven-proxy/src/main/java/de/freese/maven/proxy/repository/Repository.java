// Created: 28.12.2011
package de.freese.maven.proxy.repository;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public interface Repository {
    boolean exist(URI resource) throws Exception;

    RepositoryResponse getInputStream(URI resource) throws Exception;
}
