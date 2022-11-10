// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractListProperty<T> extends AbstractListExpression<T> implements Property<ObservableList<T>>
{
    private ObservableList<T> value;

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public ObservableList<T> getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.value.WritableValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final ObservableList<T> value)
    {
        ObservableList<T> old = this.value;
        this.value = value;

        if (!Objects.equals(old, value))
        {
            fireValueChangedEvent(old, value);
        }
    }
}
