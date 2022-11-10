// Created: 22.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.sql.JDBCType;

/**
 * Liefert den konkreten Klassentyp eines {@link JDBCType}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface TypeMapping
{
    /**
     * Liefert den konkreten Klassentyp eines {@link JDBCType}.
     */
    Type getType(JDBCType jdbcType, boolean nullable);
}
