// Created: 14.10.23
package de.freese.dependency.update.version.filter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Thomas Freese
 */
final class VersionFilterDefaultRegEx implements VersionFilter {
    private static final Pattern PATTERN_FOUR_NUMBER = Pattern.compile("\\d{1,4}[.-]\\d{1,4}[.-]\\d{1,4}[.-]\\d{1,4}");
    private static final Pattern PATTERN_ONE_NUMBER = Pattern.compile("\\d{1,4}");
    private static final Pattern PATTERN_THREE_NUMBER = Pattern.compile("\\d{1,4}[.-]\\d{1,4}[.-]\\d{1,4}");
    private static final Pattern PATTERN_TWO_NUMBER = Pattern.compile("\\d{1,4}[.-]\\d{1,4}");

    private static final Predicate<String> VERSION_FILTER =
            ((Predicate<String>) version -> version.endsWith("FINAL"))
                    .or(version -> version.endsWith("RELEASE"))
                    .or(version -> PATTERN_ONE_NUMBER.matcher(version).matches())
                    .or(version -> PATTERN_TWO_NUMBER.matcher(version).matches())
                    .or(version -> PATTERN_THREE_NUMBER.matcher(version).matches())
                    .or(version -> PATTERN_FOUR_NUMBER.matcher(version).matches());

    @Override
    public Set<String> getFilteredVersions(final String groupId, final String artifactId, final Collection<String> versions) {
        return versions.stream().filter(VERSION_FILTER).collect(Collectors.toCollection(TreeSet::new));
    }
}
