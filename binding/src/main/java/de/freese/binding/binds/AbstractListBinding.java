// Created: 08.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.AbstractListExpression;
import de.freese.binding.property.Property;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public abstract class AbstractListBinding<T> extends AbstractListExpression<T> implements ListBinding<T> {

    private ObservableList<T> value;

    @Override
    public ObservableList<T> getValue() {
        return value;
    }

    @Override
    public void update() {
        final ObservableList<T> old = value;
        value = computeValue();

        if (Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract ObservableList<T> computeValue();
}
