// Created: 29.07.2018
package de.freese.metamodel.codewriter;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JavaCodeWriter extends AbstractCodeWriter {
    @Override
    public String getFileExtension() {
        return ".java";
    }
}
