// Created: 28.12.2011
package de.freese.maven.proxy.repository;

import java.net.URI;

/**
 * Interface eines Repositories.
 *
 * @author Thomas Freese
 */
public interface Repository
{
    /**
     * Prüft, ob die Resource vorhanden ist.<br>
     */
    boolean exist(URI resource) throws Exception;

    /**
     * Laden der betreffenden Resource.<br>
     * Der Stream ist null, wenn die Resource nicht existiert.
     */
    RepositoryResponse getInputStream(URI resource) throws Exception;
}
