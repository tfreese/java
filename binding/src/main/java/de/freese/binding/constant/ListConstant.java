// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class ListConstant<T> extends AbstractListExpression<T> {

    public static <T> ListConstant<T> valueOf(final ObservableList<T> value) {
        return new ListConstant<>(value);
    }

    private final ObservableList<T> value;

    private ListConstant(final ObservableList<T> value) {
        super();

        this.value = value;
    }

    @Override
    public void addListener(final ChangeListener<? super ObservableList<T>> listener) {
        // Empty
    }

    @Override
    public ObservableList<T> getValue() {
        return this.value;
    }

    @Override
    public void removeListener(final ChangeListener<? super ObservableList<T>> listener) {
        // Empty
    }

    @Override
    protected void fireValueChangedEvent(final ObservableList<T> oldValue, final ObservableList<T> newValue) {
        // Empty
    }
}
