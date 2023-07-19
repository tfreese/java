// Created: 19.07.23
package de.freese.maven.proxy.repository.ng;

import java.net.URI;

/**
 * @author Thomas Freese
 */
public interface RemoteRepository extends Repository {

    URI getUri();
}
