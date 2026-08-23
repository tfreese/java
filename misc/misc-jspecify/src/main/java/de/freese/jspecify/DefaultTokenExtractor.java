package de.freese.jspecify;

import org.jspecify.annotations.Nullable;

/**
 * Default implementation for a {@link TokenExtractor}.
 *
 * @author Thomas Freese
 * @since 10.04.2025
 */
public final class DefaultTokenExtractor implements TokenExtractor {
    @Override
    public @Nullable String extractToken(final String input) {
        return input.contains("token") ? "token" : null;
    }
}
