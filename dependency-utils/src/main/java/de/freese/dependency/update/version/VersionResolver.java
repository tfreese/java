// Created: 26.02.2021
package de.freese.dependency.update.version;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.artifact.versioning.ComparableVersion;

import de.freese.dependency.update.version.filter.VersionFilter;
import de.freese.dependency.update.version.query.VersionQuery;

/**
 * @author Thomas Freese
 */
public class VersionResolver {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final VersionFilter versionFilter;
    private final VersionQuery versionQuery;

    public VersionResolver(final VersionQuery versionQuery) {
        this(versionQuery, VersionFilter.ofNoOp());
    }

    public VersionResolver(final VersionQuery versionQuery, final VersionFilter versionFilter) {
        super();

        this.versionQuery = Objects.requireNonNull(versionQuery, "versionQuery required");
        this.versionFilter = Objects.requireNonNull(versionFilter, "versionFilter required");
    }

    public String findNewestVersion(final String groupId, final String artifactId, final Set<URI> repositories) {
        return cache.computeIfAbsent(groupId + "_" + artifactId, key -> {
            Set<String> versions = versionQuery.getVersions(groupId, artifactId, repositories);
            versions = versionFilter.getFilteredVersions(groupId, artifactId, versions);

            return versions.stream()
                    .map(ComparableVersion::new)
                    .max(Comparator.naturalOrder())
                    .map(ComparableVersion::toString)
                    .filter(version -> !version.isBlank())
                    .orElse(VersionFilter.EMPTY_VERSION)
                    ;
        });
    }
}
