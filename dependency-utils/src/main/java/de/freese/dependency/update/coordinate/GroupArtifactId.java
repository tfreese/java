package de.freese.dependency.update.coordinate;

import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 25.10.26
 */
public class GroupArtifactId implements Comparable<GroupArtifactId> {
    private final String artifactId;
    private final String groupId;

    public GroupArtifactId(final String groupId, final String artifactId) {
        super();

        this.groupId = Objects.requireNonNull(groupId, "groupId required");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId required");
    }

    @Override
    public int compareTo(final GroupArtifactId o) {
        int comp = getGroupId().compareTo(o.getGroupId());

        if (comp == 0) {
            comp = getArtifactId().compareTo(o.getArtifactId());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final GroupArtifactId other)) {
            return false;
        }

        return Objects.equals(groupId, other.groupId) && Objects.equals(artifactId, other.artifactId);
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

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public String toString() {
        return getGroupIdAndArtifactId();
    }
}
