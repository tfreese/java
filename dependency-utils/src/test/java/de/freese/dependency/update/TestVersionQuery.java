// Created: 03.03.2019
package de.freese.dependency.update;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.dependency.EnabledIfReachable;
import de.freese.dependency.update.client.RepositoryClient;
import de.freese.dependency.update.client.jnh.JreHttpRepositoryClientBuilder;
import de.freese.dependency.update.client.url.UrlConnectionRepositoryClientBuilder;
import de.freese.dependency.update.version.query.VersionQuery;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestVersionQuery {

    @Test
    @EnabledIfReachable(uri = "https://repo.maven.apache.org/maven2", timeoutMillis = 1000)
    void testQueryMavenMetaData() throws Exception {
        try (RepositoryClient repositoryClient = new JreHttpRepositoryClientBuilder().build()) {
            final VersionQuery versionQuery = VersionQuery.ofMavenMetaData(repositoryClient);
            final Set<String> versions = versionQuery.getVersions("org.slf4j", "slf4j-api", Set.of(URI.create("https://repo.maven.apache.org/maven2")));

            assertNotNull(versions);
            assertFalse(versions.isEmpty());
        }
    }

    @Test
    @EnabledIfReachable(uri = "https://repo.maven.apache.org/maven2", timeoutMillis = 1000)
    void testQueryMavenMetaDataUrlConnection() throws Exception {
        try (RepositoryClient repositoryClient = new UrlConnectionRepositoryClientBuilder().build()) {
            final VersionQuery versionQuery = VersionQuery.ofMavenMetaData(repositoryClient);
            final Set<String> versions = versionQuery.getVersions("org.slf4j", "slf4j-api", Set.of(URI.create("https://repo.maven.apache.org/maven2")));

            assertNotNull(versions);
            assertFalse(versions.isEmpty());
        }
    }

    @Test
    // @EnabledIfReachable(uri = "file:///tmp/dummy", timeoutMillis = 0)
    @EnabledIfReachable(uri = "https://search.maven.org/solrsearch", timeoutMillis = 1000)
    void testQueryMavenSearch() throws Exception {
        try (RepositoryClient repositoryClient = new JreHttpRepositoryClientBuilder().build()) {
            final VersionQuery versionQuery = VersionQuery.ofMavenSearch(repositoryClient);
            final Set<String> versions = versionQuery.getVersions("org.slf4j", "slf4j-api", Set.of());

            assertNotNull(versions);
            assertFalse(versions.isEmpty());
        }
    }
}
