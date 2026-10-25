package de.freese.dependency.update.coordinate;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 * @since 22.03.2017
 */
public final class Coordinate extends GroupArtifactId {
    private final String source;

    private String versionCurrent;

    @Nullable
    private String versionNewest;

    public Coordinate(final String groupId, final String artifactId, final String version, final String source) {
        super(groupId, artifactId);

        this.versionCurrent = Objects.requireNonNull(version, "version required");
        this.source = Objects.requireNonNull(source, "source required");
    }

    @Override
    public int compareTo(final GroupArtifactId o) {
        int comp = super.compareTo(o);

        final Coordinate other = (Coordinate) o;

        if (comp == 0) {
            comp = getVersionCurrent().compareTo(other.getVersionCurrent());
        }

        if (comp == 0) {
            comp = getSource().compareTo(other.getSource());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Coordinate that)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        return Objects.equals(versionCurrent, that.versionCurrent) && Objects.equals(source, that.source);
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
        return Objects.hash(super.hashCode(), source, versionCurrent);
    }

    public void setVersionCurrent(final String versionCurrent) {
        this.versionCurrent = versionCurrent;
    }

    public void setVersionNewest(final @Nullable String versionNewest) {
        this.versionNewest = versionNewest;
    }

    @Override
    public String toString() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersionCurrent() + " " + getSource();
    }
}
