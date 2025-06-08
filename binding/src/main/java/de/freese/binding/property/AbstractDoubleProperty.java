// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractDoubleExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDoubleProperty extends AbstractDoubleExpression implements Property<Double> {

    private double value;

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(final Double value) {
        final double old = this.value;
        this.value = value;

        if (Double.compare(old, value) != 0) {
            fireValueChangedEvent(old, value);
        }
    }
}
