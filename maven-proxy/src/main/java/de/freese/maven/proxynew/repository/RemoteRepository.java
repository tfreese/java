// Created: 19.07.23
package de.freese.maven.proxynew.repository;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public interface RemoteRepository extends Repository {

    URI getUri();
}
