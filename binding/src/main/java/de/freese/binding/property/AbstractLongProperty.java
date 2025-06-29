// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractLongExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLongProperty extends AbstractLongExpression implements Property<Long> {

    private long value;

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(final Long value) {
        final long old = this.value;
        this.value = value;

        if (old != value) {
            fireValueChangedEvent(old, value);
        }
    }
}
