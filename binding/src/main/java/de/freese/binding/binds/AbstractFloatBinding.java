// Created: 01.08.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractFloatExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFloatBinding extends AbstractFloatExpression implements FloatBinding {

    private float value;

    @Override
    public Float getValue() {
        return this.value;
    }

    @Override
    public void update() {
        final float old = this.value;
        this.value = computeValue();

        if (Objects.equals(old, this.value)) {
            fireValueChangedEvent(old, this.value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract float computeValue();
}
