// Created: 06.03.2019
package de.freese.dependency.update.version.filter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * NO-OP {@link VersionFilter}
 *
 * @author Thomas Freese
 */
final class VersionFilterNoOp implements VersionFilter {
    @Override
    public Set<String> getFilteredVersions(final String groupId, final String artifactId, final Collection<String> versions) {
        return new TreeSet<>(versions);
    }
}
