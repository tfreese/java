package de.freese.dependency.update.coordinate;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 * @since 22.03.2017
 */
public final class Coordinate implements Comparable<Coordinate> {
    private final String artifactId;
    private final String groupId;
    private final String source;

    private String version;

    @Nullable
    private List<String> versions;

    public Coordinate(final String groupId, final String artifactId, final String version, final String source) {
        super();

        this.groupId = Objects.requireNonNull(groupId, "groupId required");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId required");
        this.version = Objects.requireNonNull(version, "version required");
        this.source = Objects.requireNonNull(source, "source required");
    }

    @Override
    public int compareTo(final Coordinate o) {
        int comp = getGroupId().compareTo(o.getGroupId());

        if (comp == 0) {
            comp = getArtifactId().compareTo(o.getArtifactId());
        }

        if (comp == 0) {
            comp = getVersion().compareTo(o.getVersion());
        }

        if (comp == 0) {
            comp = getSource().compareTo(o.getSource());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Coordinate that)) {
            return false;
        }

        return Objects.equals(groupId, that.groupId)
                && Objects.equals(artifactId, that.artifactId)
                && Objects.equals(version, that.version)
                && Objects.equals(source, that.source);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupIdAndArtifactId() {
        return getGroupId() + ":" + getArtifactId();
    }

    public String getSource() {
        return source;
    }

    public String getVersion() {
        return version;
    }

    @Nullable
    public String getVersionNewest() {
        if (versions == null || versions.isEmpty()) {
            return null;
        }

        return versions.getFirst();
    }

    public boolean hasUpdate() {
        final String newestVersion = getVersionNewest();

        if (newestVersion == null) {
            return false;
        }

        return !getVersion().equals(newestVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version, source);
    }

    public void setVersion(final String version) {
        this.version = Objects.requireNonNull(version, "version required");
    }

    public void setVersions(final @Nullable List<String> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion() + " " + getSource();
    }
}
