// Created: 19.07.23
package de.freese.maven.proxy.repository.ng;

import de.freese.maven.proxy.utils.HttpMethod;

/**
 * @author Thomas Freese
 */
public interface Repository {
    boolean canHandle(HttpMethod httpMethod);

    /**
     * The name is the context-root.
     */
    String getName();
}
