// Created: 29.07.2018
package de.freese.metamodel.codewriter;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JavaCodeWriter extends AbstractCodeWriter {
    /**
     * @see de.freese.metamodel.codewriter.AbstractCodeWriter#getFileExtension()
     */
    @Override
    public String getFileExtension() {
        return ".java";
    }
}
