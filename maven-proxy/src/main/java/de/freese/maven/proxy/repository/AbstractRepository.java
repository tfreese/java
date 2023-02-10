// Created: 28.12.2011
package de.freese.maven.proxy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines {@link Repository}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRepository implements Repository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return this.logger;
    }
}
