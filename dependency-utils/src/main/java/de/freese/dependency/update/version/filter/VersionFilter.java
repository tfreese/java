// Created: 06.03.2019
package de.freese.dependency.update.version.filter;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface VersionFilter {
    String EMPTY_VERSION = "Dependency not found !";

    static VersionFilter ofDefaultRegEx() {
        return new VersionFilterDefaultRegEx();
    }

    /**
     * Default: {@code Paths.get(System.getProperty("user.home"), ".m2", "rule-set.xml")}
     */
    static VersionFilter ofMavenRuleSet() {
        return ofMavenRuleSet(Path.of(System.getProperty("user.home"), ".m2", "rule-set.xml"));
    }

    static VersionFilter ofMavenRuleSet(final Path ruleSet) {
        final VersionFilterMavenRuleSet versionFilter = new VersionFilterMavenRuleSet();
        versionFilter.parse(ruleSet);

        return versionFilter;
    }

    static VersionFilter ofNoOp() {
        return new VersionFilterNoOp();
    }

    Set<String> getFilteredVersions(String groupId, String artifactId, Collection<String> versions);
}
