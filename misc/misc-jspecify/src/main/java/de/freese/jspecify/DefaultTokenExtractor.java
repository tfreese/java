// Created: 10 Apr. 2025
package de.freese.jspecify;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 */
public final class DefaultTokenExtractor implements TokenExtractor {
    @Override
    public @Nullable String extractToken(final String input) {
        return input.contains("token") ? "token" : null;
    }
}
