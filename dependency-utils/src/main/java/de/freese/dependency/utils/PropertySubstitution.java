package de.freese.dependency.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class PropertySubstitution {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertySubstitution.class);

    /**
     * Replace the placeholders '${...}', System.getProperty and System.getenv variables:
     *
     * <pre>
     *  hello = Hello
     *  world = World
     *  place = ${hello} ${world}
     *  sys.tmpdir = ${java.io.tmpdir}
     *  env.home = ${HOME}
     * </pre>
     *
     * Logs the unresolvable PlaceHolder.
     *
     * @return Map of unresolved PlaceHolders.
     */
    public static Map<String, String> replacePlaceHolder(final Map<String, String> map) {
        return replacePlaceHolder(map, key -> {
            String value = map.get(key);

            if (value == null) {
                value = System.getProperty(key);
            }

            if (value == null) {
                value = System.getenv(key);
            }

            return value;
        });
    }

    public static Map<String, String> replacePlaceHolder(final Map<String, String> map, final UnaryOperator<String> placeHolderToValue) {
        final Map<String, String> unresolvable = new TreeMap<>();

        map.replaceAll((key, value) -> {
            if (value == null || !value.contains("${")) {
                return value;
            }

            String newValue = value;

            if (newValue.contains("${")) {
                newValue = replacePlaceHolder(newValue, placeHolderToValue);
            }

            if (newValue.contains("${")) {
                unresolvable.put(key, value);
            }

            return newValue;
        });

        unresolvable.forEach((key, value) -> LOGGER.warn("Found unresolved Placeholder: {} = {}", key, value));

        return unresolvable;
    }

    private static List<String> getPlaceHolders(final String value) {
        final List<String> placeHolders = new ArrayList<>();

        int startIndex;
        int lastEndIndex = 0;

        while ((startIndex = value.indexOf("${", lastEndIndex)) != -1) {
            final int endIndex = value.indexOf('}', startIndex);

            if (endIndex != -1) {
                final String placeHolder = value.substring(startIndex + 2, endIndex);
                placeHolders.add(placeHolder);

                lastEndIndex = endIndex;
            }
        }

        return placeHolders;
    }

    private static String replacePlaceHolder(final String value, final UnaryOperator<String> placeHolderToValue) {
        final List<String> placeHolders = getPlaceHolders(value);

        String newValue = value;

        for (final String placeHolder : placeHolders) {
            final String placeHolderValue = placeHolderToValue.apply(placeHolder);

            if (placeHolderValue != null) {
                newValue = newValue.replace("${" + placeHolder + "}", placeHolderValue);
            }
        }

        return newValue;
    }

    private PropertySubstitution() {
        super();
    }
}
