// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

import de.freese.metamodel.Utils;

/**
 * Default-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public class DefaultNamingStrategy extends AbstractNamingStrategy
{
    /**
     * @see de.freese.metamodel.modelgen.naming.NamingStrategy#getClassName(java.lang.String)
     */
    @Override
    public String getClassName(final String tableName)
    {
        String tName = normalize(tableName);

        tName = toCamelCase(tName);

        return tName;
    }

    /**
     * @see de.freese.metamodel.modelgen.naming.NamingStrategy#getFieldName(java.lang.String)
     */
    @Override
    public String getFieldName(final String columnName)
    {
        String cName = normalize(columnName);

        cName = toCamelCase(cName);

        cName = Utils.unCapitalize(cName);

        return cName;
    }

    /**
     * @see de.freese.metamodel.modelgen.naming.AbstractNamingStrategy#normalize(java.lang.String)
     */
    @Override
    protected String normalize(final String value)
    {
        String s = super.normalize(value);

        if (s.startsWith("t_") || s.startsWith("tbl_"))
        {
            s = s.replaceFirst("t_", "").replaceFirst("tbl_", "");
        }

        return s;
    }
}
