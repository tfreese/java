package de.freese.dependency.update.client;

import java.io.Serial;

/**
 * @author Thomas Freese
 * @since 18.09.26
 */
public class RepositoryClientException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6180711915124957830L;

    public RepositoryClientException(final String message) {
        super(message);
    }

    public RepositoryClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RepositoryClientException(final Throwable cause) {
        super(cause);
    }
}
