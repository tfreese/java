package de.freese.dependency.update.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Freese
 */
public final class HostsAwareAuthenticator extends Authenticator {
    private final Map<String, PasswordAuthentication> map = new ConcurrentHashMap<>();

    public HostsAwareAuthenticator(final String targetHost, final PasswordAuthentication credentials) {
        this(Map.of(
                        Objects.requireNonNull(targetHost, "targetHost required"),
                        Objects.requireNonNull(credentials, "credentials required")
                )
        );
    }

    public HostsAwareAuthenticator(final Map<String, PasswordAuthentication> map) {
        super();

        Objects.requireNonNull(map, "map required")
                .forEach((host, credentials) -> this.map.put(host.toLowerCase(), credentials));
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        final String requestingHost = getRequestingHost();

        if (requestingHost != null) {
            return map.get(requestingHost.toLowerCase());
        }

        return null;
    }
}
