// Created: 01.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractDoubleExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDoubleBinding extends AbstractDoubleExpression implements DoubleBinding
{
    /**
    *
    */
    private double value;

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     *
     * @return double
     */
    protected abstract double computeValue();

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Double getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.binds.Binding#update()
     */
    @Override
    public void update()
    {
        double old = this.value;
        this.value = computeValue();

        if (Objects.equals(old, this.value))
        {
            fireValueChangedEvent(old, this.value);
        }
    }

}
