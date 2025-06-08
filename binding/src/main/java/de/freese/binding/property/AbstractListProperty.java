// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractListProperty<T> extends AbstractListExpression<T> implements Property<ObservableList<T>> {

    private ObservableList<T> value;

    @Override
    public ObservableList<T> getValue() {
        return value;
    }

    @Override
    public void setValue(final ObservableList<T> value) {
        final ObservableList<T> old = this.value;
        this.value = value;

        if (!Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }
}
