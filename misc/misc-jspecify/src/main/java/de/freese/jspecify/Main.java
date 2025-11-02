// Created: 10 Apr. 2025
package de.freese.jspecify;

/**
 * Main class.
 *
 * @author Thomas Freese
 */
// @NullMarked
// Or in package-info.java for complete Package.
public final class Main {
    static void main() {
        final TokenExtractor extractor = new DefaultTokenExtractor();
        final String token = extractor.extractToken("...");

        System.out.println("The token has a length of " + token.length());
    }

    private Main() {
        super();
    }
}
