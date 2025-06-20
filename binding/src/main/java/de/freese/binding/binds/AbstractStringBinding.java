// Created: 31.07.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractStringExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractStringBinding extends AbstractStringExpression implements StringBinding {

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void update() {
        final String old = value;
        value = computeValue();

        if (Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract String computeValue();
}
