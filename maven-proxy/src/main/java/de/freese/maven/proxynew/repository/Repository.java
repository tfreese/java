// Created: 19.07.23
package de.freese.maven.proxynew.repository;

import java.net.URI;

import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.HttpMethod;
import de.freese.maven.proxynew.lifecycle.Lifecycle;

/**
 * @author Thomas Freese
 */
public interface Repository extends Lifecycle {

    boolean exist(URI resource) throws Exception;

    RepositoryResponse getInputStream(URI resource) throws Exception;

    /**
     * The name is the context-root.
     */
    String getName();

    boolean supports(HttpMethod httpMethod);
}
