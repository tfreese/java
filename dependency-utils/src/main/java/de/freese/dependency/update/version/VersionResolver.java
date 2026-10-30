package de.freese.dependency.update.version;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.artifact.versioning.ComparableVersion;

import de.freese.dependency.update.version.filter.VersionFilter;
import de.freese.dependency.update.version.query.VersionQuery;

/**
 * @author Thomas Freese
 * @since 26.02.2021
 */
public class VersionResolver {
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

    public List<String> resolveVersions(final String groupId, final String artifactId, final Set<URI> repositories) {
        Set<String> versions = versionQuery.queryVersions(groupId, artifactId, repositories);
        versions = versionFilter.filterVersions(groupId, artifactId, versions);

        return versions.stream()
                .map(ComparableVersion::new)
                .sorted(Comparator.reverseOrder())
                .map(ComparableVersion::toString)
                .filter(version -> !version.isBlank())
                .limit(3L)
                .toList()
                ;
    }
}
