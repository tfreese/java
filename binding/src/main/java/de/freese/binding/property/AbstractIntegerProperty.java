// Created: 01.08.2018
package de.freese.binding.property;

import de.freese.binding.expression.AbstractIntegerExpression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIntegerProperty extends AbstractIntegerExpression implements Property<Integer> {

    private int value;

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void setValue(final Integer value) {
        int old = this.value;
        this.value = value;

        if (old != value) {
            fireValueChangedEvent(old, value);
        }
    }
}
