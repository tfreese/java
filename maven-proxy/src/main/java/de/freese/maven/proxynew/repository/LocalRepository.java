// Created: 19.07.23
package de.freese.maven.proxynew.repository;

import java.nio.file.Path;

/**
 * @author Thomas Freese
 */
public interface LocalRepository extends Repository {

    Path getPath();
}
