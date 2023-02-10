// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractLongExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class LongConstant extends AbstractLongExpression {
    public static LongConstant valueOf(final long value) {
        return new LongConstant(value);
    }

    private final long value;

    private LongConstant(final long value) {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super Long> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Long getValue() {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super Long> listener) {
        // Empty
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final Long oldValue, final Long newValue) {
        // Empty
    }
}
