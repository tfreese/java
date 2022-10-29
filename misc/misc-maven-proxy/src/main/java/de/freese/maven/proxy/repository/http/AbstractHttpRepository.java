// Created: 15.09.2019
package de.freese.maven.proxy.repository.http;

import java.net.URI;
import java.util.Objects;

import de.freese.maven.proxy.repository.AbstractRepository;

/**
 * Basisimplementierung eines HTTP-Repositories.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHttpRepository extends AbstractRepository
{
    private final URI baseUri;

    protected AbstractHttpRepository(final URI baseUri)
    {
        super();

        this.baseUri = Objects.requireNonNull(baseUri, "baseUri required");

        String scheme = baseUri.getScheme();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))
        {
            String msg = "HTTP or HTTPS protocol required: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getBaseUri().toString();
    }

    protected URI createResourceUri(final URI uri, final URI resource)
    {
        String path = uri.getPath();
        String pathResource = resource.getPath();

        if (path.endsWith("/") && pathResource.startsWith("/"))
        {
            path += pathResource.substring(1);
        }
        else if (path.endsWith("/") && !pathResource.startsWith("/"))
        {
            path += pathResource;
        }
        else if (!path.endsWith("/") && pathResource.startsWith("/"))
        {
            path += pathResource;
        }

        return uri.resolve(path);
    }

    protected URI getBaseUri()
    {
        return this.baseUri;
    }
}
