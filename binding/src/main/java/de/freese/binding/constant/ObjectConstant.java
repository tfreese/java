// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractObjectExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class ObjectConstant<T> extends AbstractObjectExpression<T> {

    public static <T> ObjectConstant<T> valueOf(final T value) {
        return new ObjectConstant<>(value);
    }

    private final T value;

    private ObjectConstant(final T value) {
        super();

        this.value = value;
    }

    @Override
    public void addListener(final ChangeListener<? super T> listener) {
        // Empty
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void removeListener(final ChangeListener<? super T> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final T oldValue, final T newValue) {
        // Empty
    }
}
