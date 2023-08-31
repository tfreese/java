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

    @Override
    public void addListener(final ChangeListener<? super Long> listener) {
        // Empty
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    @Override
    public void removeListener(final ChangeListener<? super Long> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final Long oldValue, final Long newValue) {
        // Empty
    }
}
