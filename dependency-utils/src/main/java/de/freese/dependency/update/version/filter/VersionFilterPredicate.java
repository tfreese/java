// Created: 14.10.23
package de.freese.dependency.update.version.filter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Thomas Freese
 */
final class VersionFilterPredicate implements VersionFilter {
    private Predicate<String> versionFilter = version -> false;

    public VersionFilterPredicate addVersionFilter(final Predicate<String> filter) {
        versionFilter = versionFilter.or(filter);

        return this;
    }

    @Override
    public Set<String> getFilteredVersions(final String groupId, final String artifactId, final Collection<String> versions) {
        return versions.stream().filter(versionFilter).collect(Collectors.toCollection(TreeSet::new));
    }
}
