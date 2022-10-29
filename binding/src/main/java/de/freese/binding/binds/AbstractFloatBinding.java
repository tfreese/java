// Created: 01.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractFloatExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFloatBinding extends AbstractFloatExpression implements FloatBinding
{
    /**
    *
    */
    private float value;

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     *
     * @return float
     */
    protected abstract float computeValue();

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Float getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.binds.Binding#update()
     */
    @Override
    public void update()
    {
        float old = this.value;
        this.value = computeValue();

        if (Objects.equals(old, this.value))
        {
            fireValueChangedEvent(old, this.value);
        }
    }

}
