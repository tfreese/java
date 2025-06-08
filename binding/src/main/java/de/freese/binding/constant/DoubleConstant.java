// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractDoubleExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class DoubleConstant extends AbstractDoubleExpression {

    public static DoubleConstant valueOf(final double value) {
        return new DoubleConstant(value);
    }

    private final double value;

    private DoubleConstant(final double value) {
        super();

        this.value = value;
    }

    @Override
    public void addListener(final ChangeListener<? super Double> listener) {
        // Empty
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void removeListener(final ChangeListener<? super Double> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final Double oldValue, final Double newValue) {
        // Empty
    }
}
