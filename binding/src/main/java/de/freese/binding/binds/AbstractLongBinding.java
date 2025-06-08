// Created: 01.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractLongExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLongBinding extends AbstractLongExpression implements LongBinding {

    private long value;

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void update() {
        final long old = value;
        value = computeValue();

        if (Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract long computeValue();
}
