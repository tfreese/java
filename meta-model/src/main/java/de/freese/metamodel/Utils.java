// Created: 18.09.2021
package de.freese.metamodel;

/**
 * @author Thomas Freese
 */
public final class Utils
{
    /**
     * @param value String
     *
     * @return String
     */
    public static String capitalize(final String value)
    {
        if ((value == null) || value.isBlank())
        {
            return value;
        }

        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    /**
     * @param value String
     *
     * @return String
     */
    public static String unCapitalize(final String value)
    {
        if ((value == null) || value.isBlank())
        {
            return value;
        }

        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    /**
     * Erstellt ein neues {@link Utils} Object.
     */
    private Utils()
    {
        super();
    }
}
