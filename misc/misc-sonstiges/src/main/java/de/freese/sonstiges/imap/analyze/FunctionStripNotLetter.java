// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Diese {@link Function} entfernt alle Zeichen, die keine Lettern sind {@link Character#isLetter(char)}.<br>
 * Die entfernten Zeichen werden durch ' ' ersetzt.<br>
 *
 * @author Thomas Freese
 */
public class FunctionStripNotLetter implements UnaryOperator<String> {

    public static final Function<String, String> INSTANCE = new FunctionStripNotLetter();

    @Override
    public String apply(final String text) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);

            // A-Z a-z
            // if (((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122)))
            if (Character.isLetter(c)) {
                sb.append(c);
            }
            else {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
