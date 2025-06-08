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

    @Override
    public void addListener(final ChangeListener<? super Boolean> listener) {
        // Empty
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void removeListener(final ChangeListener<? super Boolean> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final Boolean oldValue, final Boolean newValue) {
        // Empty
    }
}
