// Created: 01.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractIntegerExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIntegerBinding extends AbstractIntegerExpression implements IntegerBinding {

    private int value;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void update() {
        final int old = value;
        value = computeValue();

        if (Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract int computeValue();
}
