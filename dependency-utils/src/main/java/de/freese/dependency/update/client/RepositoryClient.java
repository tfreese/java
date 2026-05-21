// Created: 03 Apr. 2025
package de.freese.dependency.update.client;

import java.net.URI;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface RepositoryClient extends AutoCloseable {

    @Override
    void close();

    boolean exist(URI uri);

    /**
     * <a href="https://search.maven.org/solrsearch/select?q=g:org.springframework.boot+AND+a:spring-boot-dependencies&core=gav&rows=10&wt=json">spring-boot-dependencies</a>
     */
    List<String> getVersionsByMavenSearch(URI uri);

    /**
     * <a href="https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml">spring-boot-dependencies</a>
     */
    List<String> getVersionsByMetaData(URI uri);
}
