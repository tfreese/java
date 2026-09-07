package de.freese.jspecify;

import org.jspecify.annotations.Nullable;

/**
 * Extractor for Tokens.
 *
 * @author Thomas Freese
 * @since 10.04.2025
 */
public interface TokenExtractor {
    @Nullable
    String extractToken(String input);
}
