// Created: 10 Apr. 2025
package de.freese.jspecify;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 */
public interface TokenExtractor {
    @Nullable
    String extractToken(String input);
}
