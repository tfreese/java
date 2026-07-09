// Created: 22.03.2017
package de.freese.dependency.update.coordinate;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 */
public final class Coordinate implements Comparable<Coordinate> {
    private final String artifactId;
    private final String groupId;
    private final String source;

    private String versionCurrent;

    @Nullable
    private String versionNewest;

    public Coordinate(final String groupId, final String artifactId, final String version, final String source) {
        super();

        this.groupId = Objects.requireNonNull(groupId, "groupId required");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId required");
        this.versionCurrent = Objects.requireNonNull(version, "version required");
        this.source = Objects.requireNonNull(source, "source required");
    }

    @Override
    public int compareTo(final Coordinate o) {
        int comp = getGroupId().compareTo(o.getGroupId());

        if (comp == 0) {
            comp = getArtifactId().compareTo(o.getArtifactId());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof final Coordinate other)) {
            return false;
        }

        return Objects.equals(artifactId, other.artifactId)
                && Objects.equals(groupId, other.groupId)
                && Objects.equals(source, other.source);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupIdAndArtifactId() {
        return String.format("%s:%s", getGroupId(), getArtifactId());
    }

    public String getSource() {
        return source;
    }

    public String getVersionCurrent() {
        return versionCurrent;
    }

    @Nullable
    public String getVersionNewest() {
        return versionNewest;
    }

    public boolean hasUpdate() {
        return !getVersionCurrent().equals(getVersionNewest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, groupId, source);
    }

    public void setVersionCurrent(final String versionCurrent) {
        this.versionCurrent = versionCurrent;
    }

    public void setVersionNewest(final String versionNewest) {
        this.versionNewest = versionNewest;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s %s", getGroupId(), getArtifactId(), getVersionCurrent(), getSource());
    }
}
