// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

/**
 * Interface für die Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public interface NamingStrategy {
    /**
     * Konvertiert einen Tabellen-Namen in einen Klassen-Namen.
     */
    String getClassName(String tableName);

    /**
     * Konvertiert einen Spalten-Namen in einen Attribut-Namen.
     */
    String getFieldName(String columnName);
}
