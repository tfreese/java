// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.expression.AbstractBooleanExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractBooleanProperty extends AbstractBooleanExpression implements Property<Boolean> {

    private Boolean value;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(final Boolean value) {
        final Boolean old = this.value;
        this.value = value;

        if (!Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }
}
