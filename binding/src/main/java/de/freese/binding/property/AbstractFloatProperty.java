// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractFloatExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFloatProperty extends AbstractFloatExpression implements Property<Float> {

    private float value;

    @Override
    public Float getValue() {
        return this.value;
    }

    @Override
    public void setValue(final Float value) {
        float old = this.value;
        this.value = value;

        if (Float.compare(old, value) != 0) {
            fireValueChangedEvent(old, value);
        }
    }
}
