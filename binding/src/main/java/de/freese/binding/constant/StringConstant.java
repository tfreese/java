// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractStringExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class StringConstant extends AbstractStringExpression {

    public static StringConstant valueOf(final String value) {
        return new StringConstant(value);
    }

    private final String value;

    private StringConstant(final String value) {
        super();

        this.value = value;
    }

    @Override
    public void addListener(final ChangeListener<? super String> listener) {
        // Empty
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void removeListener(final ChangeListener<? super String> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final String oldValue, final String newValue) {
        // Empty
    }
}
