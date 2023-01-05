// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Diese {@link Function} ersetzt die Deutschen Umlaute.<br>
 * <ul>
 * <li>'ß' -> 'ss'
 * <li>'ae' -> 'ä'
 * <li>'oe' -> 'ö'
 * <li>'ue' -> 'ü', wird nicht ersetzt, wenn darauf ein Vokal oder 'q' folgt
 * </ul>
 *
 * @author Thomas Freese
 */
public class FunctionNormalizeGerman implements UnaryOperator<String>
{
    public static final UnaryOperator<String> INSTANCE = new FunctionNormalizeGerman();

    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    @SuppressWarnings("checkstyle:EmptyBlock")
    public String apply(final String text)
    {
        String t = text.replace("ß", "ss");
        t = t.replace("ae", "ä");
        t = t.replace("oe", "ö");

        int index = t.indexOf("ue");

        if ((index > 0) && ((index + 2) < t.length()))
        {
            char c = t.charAt(index + 2);

            if ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u') || (c == 'q'))
            {
                // Empty
            }
            else
            {
                t = t.replace("ue", "ü");
            }
        }

        return t;
    }
}
