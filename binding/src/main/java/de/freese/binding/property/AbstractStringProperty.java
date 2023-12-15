// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.expression.AbstractStringExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractStringProperty extends AbstractStringExpression implements Property<String> {

    private String value;

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(final String value) {
        final String old = this.value;
        this.value = value;

        if (!Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }
}
