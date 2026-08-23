package de.freese.jspecify;

/**
 * @author Thomas Freese
 * @since 10.04.2025
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
