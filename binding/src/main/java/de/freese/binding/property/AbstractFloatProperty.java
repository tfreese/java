// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractFloatExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFloatProperty extends AbstractFloatExpression implements Property<Float>
{
    private float value;

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Float getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.value.WritableValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Float value)
    {
        float old = this.value;
        this.value = value;

        if (Float.compare(old, value) != 0)
        {
            fireValueChangedEvent(old, value);
        }
    }
}
