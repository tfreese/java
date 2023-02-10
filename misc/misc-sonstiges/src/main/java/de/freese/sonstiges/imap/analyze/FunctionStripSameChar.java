// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Diese {@link Function} liefert einen leeren String, wenn dieser nur aus demselben Zeichen besteht.<br>
 *
 * @author Thomas Freese
 */
public class FunctionStripSameChar implements UnaryOperator<String> {
    public static final Function<String, String> INSTANCE = new FunctionStripSameChar();

    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    public String apply(final String text) {
        String str = "";

        if (text.length() == 0) {
            return str;
        }

        char c = text.charAt(0);

        for (int i = 0; i < text.length(); i++) {
            if (c != text.charAt(i)) {
                str = text;
                break;
            }
        }

        return str;
    }
}
