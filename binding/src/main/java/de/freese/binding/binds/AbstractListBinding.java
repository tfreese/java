// Created: 08.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;
import de.freese.binding.property.Property;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public abstract class AbstractListBinding<T> extends AbstractListExpression<T> implements ListBinding<T>
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
     * @see de.freese.binding.binds.Binding#update()
     */
    @Override
    public void update()
    {
        ObservableList<T> old = this.value;
        this.value = computeValue();

        if (Objects.equals(old, this.value))
        {
            fireValueChangedEvent(old, this.value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract ObservableList<T> computeValue();
}
