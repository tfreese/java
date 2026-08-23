package de.freese.metamodel.codewriter;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 * @since 29.07.2018
 */
public class JavaCodeWriter extends AbstractCodeWriter {
    @Override
    public String getFileExtension() {
        return ".java";
    }
}
