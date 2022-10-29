// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

/**
 * Interface für die Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public interface NamingStrategy
{
    /**
     * Konvertiert einen Tabellen-Namen in einen Klassen-Namen.
     *
     * @param tableName String
     *
     * @return String
     */
    String getClassName(String tableName);

    /**
     * Konvertiert einen Spalten-Namen in einen Attribut-Namen.
     *
     * @param columnName String
     *
     * @return String
     */
    String getFieldName(String columnName);
}
