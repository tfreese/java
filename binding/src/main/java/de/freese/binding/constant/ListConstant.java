// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public final class ListConstant<T> extends AbstractListExpression<T>
{
    /**
     * @param value ObservableList
     *
     * @return {@link ListConstant}
     */
    public static <T> ListConstant<T> valueOf(final ObservableList<T> value)
    {
        return new ListConstant<>(value);
    }

    /**
     *
     */
    private final ObservableList<T> value;

    /**
     * Erstellt ein neues {@link ListConstant} Object.
     *
     * @param value {@link String}
     */
    private ListConstant(final ObservableList<T> value)
    {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super ObservableList<T>> listener)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.expression.AbstractListExpression#fireValueChangedEvent(de.freese.binding.collections.ObservableList,
     *      de.freese.binding.collections.ObservableList)
     */
    @Override
    protected void fireValueChangedEvent(final ObservableList<T> oldValue, final ObservableList<T> newValue)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public ObservableList<T> getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super ObservableList<T>> listener)
    {
        // NO-OP
    }
}
