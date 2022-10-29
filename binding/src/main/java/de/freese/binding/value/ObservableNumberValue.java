// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface ObservableNumberValue<T extends Number> extends ObservableValue<T>
{
    /**
     * Liefert den double-Wert.
     *
     * @return double
     */
    default double doubleValue()
    {
        return getValue() == null ? 0 : getValue().doubleValue();
    }

    /**
     * Liefert den float-Wert.
     *
     * @return float
     */
    default float floatValue()
    {
        return getValue() == null ? 0 : getValue().floatValue();
    }

    /**
     * Liefert den int-Wert.
     *
     * @return int
     */
    default int intValue()
    {
        return getValue() == null ? 0 : getValue().intValue();
    }

    /**
     * Liefert den long-Wert.
     *
     * @return long
     */
    default long longValue()
    {
        return getValue() == null ? 0 : getValue().longValue();
    }
}
