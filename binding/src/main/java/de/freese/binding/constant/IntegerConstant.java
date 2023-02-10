// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractIntegerExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class IntegerConstant extends AbstractIntegerExpression {
    public static IntegerConstant valueOf(final int value) {
        return new IntegerConstant(value);
    }

    private final int value;

    private IntegerConstant(final int value) {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super Integer> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Integer getValue() {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super Integer> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final Integer oldValue, final Integer newValue) {
        // Empty
    }
}
