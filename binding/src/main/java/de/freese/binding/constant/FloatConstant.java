// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractFloatExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class FloatConstant extends AbstractFloatExpression {

    public static FloatConstant valueOf(final float value) {
        return new FloatConstant(value);
    }

    private final float value;

    private FloatConstant(final float value) {
        super();

        this.value = value;
    }

    @Override
    public void addListener(final ChangeListener<? super Float> listener) {
        // Empty
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void removeListener(final ChangeListener<? super Float> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final Float oldValue, final Float newValue) {
        // Empty
    }
}
