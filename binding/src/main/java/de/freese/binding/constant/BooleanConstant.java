// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractBooleanExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class BooleanConstant extends AbstractBooleanExpression {
    public static BooleanConstant valueOf(final boolean value) {
        return new BooleanConstant(value);
    }

    private final boolean value;

    private BooleanConstant(final boolean value) {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super Boolean> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Boolean getValue() {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super Boolean> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final Boolean oldValue, final Boolean newValue) {
        // Empty
    }
}
