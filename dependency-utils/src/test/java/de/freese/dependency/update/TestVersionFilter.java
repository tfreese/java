// Created: 03.03.2019
package de.freese.dependency.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import de.freese.dependency.update.version.filter.VersionFilter;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestVersionFilter {

    @AfterAll
    static void afterAll() {
        // Empty
    }

    @BeforeAll
    static void beforeAll() {
        if (Files.exists(Path.of(System.getProperty("user.home"), ".m2", "rule-set.xml"))) {
            System.setProperty("rule-set.xml.exist", "true");
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "rule-set.xml.exist", matches = "true")
    void testFilterMavenRuleSetGroupIdArtefactId() {
        // List<String> versions = Arrays.asList("1.0.1", "20190307", "v20190307", "1.11.513");
        final List<String> versions = Arrays.asList("1.11.510", "1.11.513");

        final VersionFilter versionFilter = VersionFilter.ofMavenRuleSet();

        final Set<String> filtered = versionFilter.getFilteredVersions("com.amazonaws", "aws-java-sdk", versions);

        assertNotNull(filtered);
        assertTrue(filtered.size() > 1);
        assertTrue(filtered.contains("1.11.510"));
    }

    @Test
    void testFilterRegEx() {
        final List<String> versions = Arrays.asList("21", "1.0.1", "v20190307", "1.11.510");
        final VersionFilter versionFilter = VersionFilter.ofDefaultRegEx();

        final Set<String> filtered = versionFilter.getFilteredVersions(null, null, versions);

        assertNotNull(filtered);
        assertEquals(3, filtered.size());
        assertTrue(filtered.contains("21"));
        assertTrue(filtered.contains("1.0.1"));
        assertTrue(filtered.contains("1.11.510"));
    }
}
