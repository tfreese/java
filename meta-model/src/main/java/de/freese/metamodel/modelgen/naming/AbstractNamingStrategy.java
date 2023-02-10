// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

/**
 * Basis-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public abstract class AbstractNamingStrategy implements NamingStrategy {
    /**
     * Ersetzt folgende Zeichen durch Leerzeichen:<br>
     * - \r<br>
     * - \n<br>
     * und führt ein trim und toLowerCase durch.
     */
    protected String normalize(final String value) {
        if (value != null) {
            return value.strip().replace('\r', ' ').replace('\n', ' ').toLowerCase();
        }

        return null;
    }

    protected String toCamelCase(final String value) {
        if (value == null) {
            return null;
        }

        String str = value.strip();

        if (str.length() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            if (i == 0) {
                builder.append(Character.toUpperCase(str.charAt(i)));
            }
            else if ((i < (str.length() - 1)) && ((str.charAt(i) == '_') || (str.charAt(i) == '-') || (str.charAt(i) == ' '))) {
                i += 1;

                if (i < str.length()) {
                    builder.append(Character.toUpperCase(str.charAt(i)));
                }
            }
            else {
                builder.append(str.charAt(i));
            }
        }

        return builder.toString();
    }
}
