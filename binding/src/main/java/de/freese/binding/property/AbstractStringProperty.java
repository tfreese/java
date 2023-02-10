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

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * @see de.freese.binding.value.WritableValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final String value) {
        String old = this.value;
        this.value = value;

        if (!Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }
}
