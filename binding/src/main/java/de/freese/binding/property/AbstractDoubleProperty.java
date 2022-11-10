// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractDoubleExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDoubleProperty extends AbstractDoubleExpression implements Property<Double>
{
    private double value;

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Double getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.value.WritableValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Double value)
    {
        double old = this.value;
        this.value = value;

        if (Double.compare(old, value) != 0)
        {
            fireValueChangedEvent(old, value);
        }
    }
}
