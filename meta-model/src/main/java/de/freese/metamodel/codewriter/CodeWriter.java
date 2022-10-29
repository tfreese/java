// Created: 26.07.2018
package de.freese.metamodel.codewriter;

import java.io.PrintStream;

import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * Erzeugt aus den MetaDaten den Quellcode.
 *
 * @author Thomas Freese
 */
public interface CodeWriter
{
    /**
     * Liefert die Dateiendung.
     *
     * @return String
     */
    String getFileExtension();

    /**
     * Schreibt den Code einer Klasse.
     *
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void write(ClassModel classModel, PrintStream output) throws Exception;
}
