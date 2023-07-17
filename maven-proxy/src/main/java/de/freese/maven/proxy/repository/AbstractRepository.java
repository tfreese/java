// Created: 28.12.2011
package de.freese.maven.proxy.repository;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractRepository implements Repository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return this.logger;
    }

    protected Path toRelativePath(final URI resource) {
        String uriPath = resource.getPath();
        uriPath = uriPath.replace(' ', '_');

        if (uriPath.startsWith("/")) {
            uriPath = uriPath.substring(1);
        }

        return Paths.get(uriPath);
    }
}
