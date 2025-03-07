// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Basis-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public abstract class AbstractNamingStrategy implements NamingStrategy {
    private static final Pattern PATTERN_SPLIT = Pattern.compile("[-_ ]");

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

        final String str = value.strip();

        if (str.isEmpty()) {
            return "";
        }

        return PATTERN_SPLIT.splitAsStream(value)
                .map(split -> split.substring(0, 1).toUpperCase() + split.substring(1))
                .collect(Collectors.joining());
    }
}
