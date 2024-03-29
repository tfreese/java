// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

import de.freese.metamodel.Utils;

/**
 * Default-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public class DefaultNamingStrategy extends AbstractNamingStrategy {
    @Override
    public String getClassName(final String tableName) {
        String tName = normalize(tableName);

        tName = toCamelCase(tName);

        return tName;
    }

    @Override
    public String getFieldName(final String columnName) {
        String cName = normalize(columnName);

        cName = toCamelCase(cName);

        cName = Utils.unCapitalize(cName);

        return cName;
    }

    @Override
    protected String normalize(final String value) {
        String s = super.normalize(value);

        if (s.startsWith("t_") || s.startsWith("tbl_")) {
            s = s.replaceFirst("t_", "").replaceFirst("tbl_", "");
        }

        return s;
    }
}
